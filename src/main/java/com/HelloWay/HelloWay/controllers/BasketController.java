package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.response.ProductQuantity;
import com.HelloWay.HelloWay.payload.response.QuantitysProduct;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.services.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/baskets")
public class BasketController {

    BasketService basketService;
    ProductService productService;
    BasketProductService basketProductService;

    private final CommandService commandService;

    private  UserService userService;

    private BoardService boardService ;

    private NotificationService notificationService;
    @Autowired
    public BasketController(UserService userService,
                            BasketService basketService,
                            BasketProductService basketProductService,
                            ProductService productService,
                            CommandService commandService,
                            BoardService boardService,
                            NotificationService notificationService) {
        this.basketService = basketService;
        this.basketProductService = basketProductService;
        this.productService = productService;
        this.commandService = commandService;
        this.boardService = boardService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Basket addNewBasket(@RequestBody Basket basket) {
        return basketService.addNewBasket(basket);
    }


    @PostMapping("/tables/{boardId}/baskets")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    public ResponseEntity<?> addBasketToBoard(@PathVariable Long boardId, @RequestBody Basket basket) {
        Board table = boardService.findBoardById(boardId);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }

        basket.setBoard(table);
        Basket basketObject = basketService.addNewBasket(basket);

        return ResponseEntity.ok(basketObject);
    }


    @JsonIgnore
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public List<Basket> allBaskets(){
        return basketService.findAllBaskets();
    }


    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public Basket findBasketById(@PathVariable("id") long id){
        return basketService.findBasketById(id);
    }


    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public void updateBasket(@RequestBody Basket basket){
        basketService.updateBasket(basket); }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER')")
    @ResponseBody
    public void deleteBasket(@PathVariable("id") long id){
        basketService.deleteBasket(id); }

    @PostMapping("/add/product/{productId}/to_basket/{basketId}/quantity/{quantity}")
    @PreAuthorize("hasAnyRole('GUEST','USER')")
    public ResponseEntity<?> addProductToBasket(@PathVariable long basketId, @PathVariable long productId, @PathVariable int quantity) {
        Basket basket = basketService.findBasketById(basketId);
        if (basket == null){
            return ResponseEntity.badRequest().body("basket doesn't exist");
        }
        Product product = productService.findProductById(productId);
        if (product == null){
            return ResponseEntity.badRequest().body("product doesn't exist");
        }
        basketProductService.addProductToBasket(basket, product,quantity);
        Map<Product, QuantitysProduct> productQuantityMap= basketProductService.getProductsQuantityByBasketId(basketId);
        List<ProductQuantity> productQuantities = new ArrayList<>() ;
        for (Product p : productQuantityMap.keySet()){
            productQuantities.add(new ProductQuantity(p, productQuantityMap.get(p).getOldQuantity(), productQuantityMap.get(p).getQuantity()));
        }
        return ResponseEntity.ok().body(productQuantities);
    }

    // with update of the quantity
    @PostMapping("/delete/one/product/{productId}/from_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER')")
    public ResponseEntity<?> deleteOneProductFromBasket(@PathVariable long basketId, @PathVariable long productId) {
        basketProductService.deleteProductFromBasket(basketId, productId);
        return ResponseEntity.ok().body("product deleted with success");
    }

    // delete the product from the basket what ever the quantity
    @PostMapping("/delete/product/{productId}/from_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER')")
    public ResponseEntity<?> deleteProductFromBasket(@PathVariable long basketId, @PathVariable long productId) {
        basketProductService.deleteProductFromBasketV2(basketId, productId);
        return ResponseEntity.ok().body("product deleted with success");
    }

    @PostMapping("/{basketId}/commands")
    @PreAuthorize("hasAnyRole('GUEST','USER')")
    public ResponseEntity<Command> createCommand(@PathVariable Long basketId) {
        Basket basket = basketService.findBasketById(basketId);
        Command command = commandService.createCommand(new Command());
        basketService.assignCommandToBasket(basketId, command);
        return ResponseEntity.ok(command);
    }

    @PostMapping("/{basketId}/commands/add/user/{userId}")
    @PreAuthorize("hasAnyRole('GUEST','USER')")
    public ResponseEntity<Command> createCommandWithServer(@PathVariable Long basketId, @PathVariable long userId) {
        Basket basket = basketService.findBasketById(basketId);
       /* List<BasketProduct> basketProducts = basketProductService.getBasketProductsByBasketId(basketId);
        for (BasketProduct basketProduct : basketProducts){
           basketProduct.setOldQuantity(basketProduct.getQuantity());
            basketProductService.updateBasketProduct(basketProduct);
        }
        */
        Board board = basket.getBoard();
        User user = userService.findUserById(userId);
        List<User> servers = board.getZone().getServers();
        User currentAvailableServer = servers.get(0);
        if (commandService.getLastServerWithBoardIdForCommand().get(board.getZone().getIdZone().toString()) != null){
        User lastServer = userService.findUserById(Long.parseLong(commandService.getLastServerWithBoardIdForCommand().get(board.getZone().getIdZone().toString())));
        int indexOfTheLastServer = servers.indexOf(lastServer);
        if (indexOfTheLastServer != servers.size() - 1) {
            currentAvailableServer = servers.get(indexOfTheLastServer + 1);
        }
        }

        Command command = commandService.createCommand(new Command());
        basketService.assignCommandToBasket(basketId, command);
        commandService.setServerForCommand(command.getIdCommand(), currentAvailableServer);
        command.setUser(user);
        commandService.updateCommand(command);

        String messageForTheServer = "New command placed by the table number : " + command.getBasket().getBoard().getNumTable();
        String messageForTheUser = "Your command has been placed successfully";
        notificationService.createNotification("Command Notification", messageForTheServer, command.getServer());
        notificationService.createNotification("Command Notification",messageForTheUser, command.getUser());


        return ResponseEntity.ok(command);
    }

    //Get products by id basket : done
    @GetMapping("/products/by_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER')")
    public ResponseEntity<?> getProductsByIdBasket(@PathVariable long basketId){
        Basket basket = basketService.findBasketById(basketId);
        if (basket == null){
            return  ResponseEntity.badRequest().body("basket doesn't exist with this id");
        }
        Map<Product,QuantitysProduct> productQuantityMap= basketProductService.getProductsQuantityByBasketId(basketId);
        List<ProductQuantity> productQuantities = new ArrayList<>() ;
        for (Product p : productQuantityMap.keySet()){
            productQuantities.add(new ProductQuantity(p, productQuantityMap.get(p).getOldQuantity(), productQuantityMap.get(p).getQuantity()));
        }
        return ResponseEntity.ok().body(productQuantities);
    }

    @GetMapping("/latest/basket/by_table/{tableId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER')")
    public ResponseEntity<?> getLatestBasketByIdTable(@PathVariable long tableId){
        Board board = boardService.findBoardById(tableId);
        if (board == null){
            return  ResponseEntity.badRequest().body("board doesn't exist with this id");
        }
        List<Basket> baskets = board.getBaskets();
        Basket basket = baskets.get(baskets.size() - 1);

        return ResponseEntity.ok(basket);
    }

    @GetMapping("/product/quantity/{productId}/by_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER')")
    public ResponseEntity<?> getProductQuantityByIdBasketAndIdProduct(@PathVariable long basketId, @PathVariable long productId){
        Basket basket = basketService.findBasketById(basketId);
        QuantitysProduct quantity = new QuantitysProduct(0, 0) ;
        if (basket == null){
            return  ResponseEntity.badRequest().body("basket doesn't exist with this id");
        }
        Product product = productService.findProductById(productId);
        if (product == null){
            return  ResponseEntity.badRequest().body("product doesn't exist in this basket");
        }
        Map<Product,QuantitysProduct> productQuantityMap= basketProductService.getProductsQuantityByBasketId(basketId);
        List<ProductQuantity> productQuantities = new ArrayList<>() ;
        for (Product p : productQuantityMap.keySet()){
            if (p.equals(product)){
                quantity = productQuantityMap.get(p) ;
            }
        }
        return ResponseEntity.ok().body(quantity);
    }

    //TODO : getProductsByIdCommand() : product , oldQuantity , Quantity :: Done
    @GetMapping("/products/by_command/{commandId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER')")
    public ResponseEntity<?> getProductsByIdCommand(@PathVariable long commandId){
        Command command = commandService.findCommandById(commandId);
        if (command == null){
            return  ResponseEntity.badRequest().body("command doesn't exist with this id");
        }
        Basket basket = command.getBasket();
        if (basket == null){
            return  ResponseEntity.badRequest().body("basket doesn't exist with this id");
        }
        Map<Product,QuantitysProduct> productQuantityMap= basketProductService.getProductsQuantityByBasketId(basket.getId_basket());
        List<ProductQuantity> productQuantities = new ArrayList<>() ;
        for (Product p : productQuantityMap.keySet()){
            productQuantities.add(new ProductQuantity(p, productQuantityMap.get(p).getOldQuantity(), productQuantityMap.get(p).getQuantity()));
        }
        return ResponseEntity.ok().body(productQuantities);
    }
}
