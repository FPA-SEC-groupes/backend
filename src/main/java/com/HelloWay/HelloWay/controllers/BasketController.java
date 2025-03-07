package com.HelloWay.HelloWay.controllers;

import com.HelloWay.HelloWay.config.CommandWebSocketHandler;
import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.payload.response.ProductQuantity;
import com.HelloWay.HelloWay.payload.response.QuantitysProduct;
import com.HelloWay.HelloWay.repos.CommandRepository;
import com.HelloWay.HelloWay.repos.UserRepository;
import com.HelloWay.HelloWay.services.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import static com.HelloWay.HelloWay.entities.Status.UPDATED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/baskets")
public class BasketController {
    @Autowired
    private MessageSource messageSource;
    private final BasketService basketService;
    private final ProductService productService;
    private final BasketProductService basketProductService;
    private final CommandService commandService;
    private final UserService userService;
    private final BoardService boardService;
    private final NotificationService notificationService;
    private final CommandWebSocketHandler commandWebSocketHandler;
    private final CommandRepository commandRepository ;
    @Autowired
    public BasketController(UserService userService,
                            BasketService basketService,
                            BasketProductService basketProductService,
                            ProductService productService,
                            CommandService commandService,
                            BoardService boardService,
                            NotificationService notificationService,
                            CommandWebSocketHandler commandWebSocketHandler,
                            CommandRepository commandRepository) {
        this.basketService = basketService;
        this.basketProductService = basketProductService;
        this.productService = productService;
        this.commandService = commandService;
        this.boardService = boardService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.commandWebSocketHandler = commandWebSocketHandler;
        this.commandRepository=commandRepository;
    }

    @PostMapping("/{basketId}/commands/add/user/{userId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','PROVIDER')")
    public ResponseEntity<Command> createCommandWithServer(@PathVariable Long basketId, @PathVariable long userId) {
        Basket basket = basketService.findBasketById(basketId);
        Board board = basket.getBoard();
        User user = userService.findUserById(userId);
        User server = board.getZone().getServer();
        Space space = server.getSpace();
        if (server == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Or handle this case as per your requirements
        }

        Command command1 = commandService.findCommandByBasketId(basketId);
        if (command1 != null) {
            command1.setStatus(UPDATED);
            commandService.updateCommand(command1);

            if (command1.getServer() != null) {
                Locale userLocale = new Locale(command1.getServer().getPreferredLanguage());
                Locale serverLocale = new Locale(command1.getServer().getPreferredLanguage());

                String commandeTitle = messageSource.getMessage("ntCommandeTitle", null, serverLocale);
                String clientCommandeTitle = messageSource.getMessage("ntCommandeTitle", null, userLocale);

                String messageForTheServer = messageSource.getMessage("ntComandeUpdate", null, serverLocale) 
                    + command1.getBasket().getBoard().getNumTable();
                String messageForTheUser = messageSource.getMessage("ntClientCommandeUpdate", null, userLocale);

                List<String> parames = new ArrayList<>();
                List<String> paramesClient = new ArrayList<>();
                parames.add(String.valueOf(command1.getBasket().getBoard().getNumTable()));

                notificationService.createNotification("ntCommandeTitle", "ntCommandeNew", parames, command1.getServer());
                notificationService.createNotification("ntCommandeTitle", "ntClientComandeNew", paramesClient, command1.getUser());
                commandWebSocketHandler.sendMessageToAll(new TextMessage("Update command effect: " + command1.getIdCommand()));
            }

            return ResponseEntity.ok(command1);
        }

        // If no existing command is found, create a new one
        Command command = commandService.createCommand(new Command());
        basketService.assignCommandToBasket(basketId, command);
        commandService.setServerForCommand(command.getIdCommand(), server);
        command.setUser(user);
        command.setSpace(space);
        commandService.updateCommand(command);
        
        Locale userLocale = new Locale(server.getPreferredLanguage());
        Locale serverLocale = new Locale(server.getPreferredLanguage());

        String commandeTitle = messageSource.getMessage("ntCommandeTitle", null, serverLocale);
        String clientCommandeTitle = messageSource.getMessage("ntCommandeTitle", null, userLocale);

        String messageForTheServer = messageSource.getMessage("ntCommandeNew", null, serverLocale) 
            + command.getBasket().getBoard().getNumTable();
        String messageForTheUser = messageSource.getMessage("ntClientComandeNew", null, userLocale);

        List<String> parames = new ArrayList<>();
        List<String> paramesClient = new ArrayList<>();
        parames.add(String.valueOf(command.getBasket().getBoard().getNumTable()));

        notificationService.createNotification("ntCommandeTitle", "ntCommandeNew", parames, command.getServer());
        notificationService.createNotification("ntCommandeTitle", "ntClientComandeNew", paramesClient, command.getUser());
        commandWebSocketHandler.sendMessageToAll(new TextMessage("New command created: " + command.getIdCommand()));

        return ResponseEntity.ok(command);
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
        basketService.deleteBasket(id); 
    }

        @PostMapping("/add/product/{productId}/to_basket/{basketId}/quantity/{quantity}")
        @PreAuthorize("hasAnyRole('GUEST','USER','PROVIDER')")
        public ResponseEntity<?> addProductToBasket(@PathVariable long basketId, @PathVariable long productId, @PathVariable int quantity) {
            Basket basket = basketService.findBasketById(basketId);
            if (basket == null){
                return ResponseEntity.badRequest().body("Basket doesn't exist with this id");
            }
            
            Product product = productService.findProductById(productId);
            if (product == null){
                return ResponseEntity.badRequest().body("Product doesn't exist with this id");
            }
            
            basketProductService.addProductToBasket(basket, product, quantity);
            
            Map<Product, QuantitysProduct> productQuantityMap = basketProductService.getProductsQuantityByBasketId(basketId);
            List<ProductQuantity> productQuantities = new ArrayList<>();
            
            for (Map.Entry<Product, QuantitysProduct> entry : productQuantityMap.entrySet()) {
                Product p = entry.getKey();
                QuantitysProduct quantitiesProduct = entry.getValue();
                ProductStatus status = determineProductStatus(basket,p);
                productQuantities.add(new ProductQuantity(p, quantitiesProduct.getOldQuantity(), quantitiesProduct.getQuantity(), status));
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getAuthorities() != null) {
                boolean isProvider = authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_PROVIDER"));
                if (isProvider) {
                    // Basket basket = basketService.findBasketById(basketId);
                    Command command = basket.getCommand();
                    command.setStatus(UPDATED);
                    User user =command.getUser();
                    Board board = basket.getBoard();
                    User server = board.getZone().getServer();
                    Locale userLocale = new Locale(user.getPreferredLanguage());
                    Locale ServerLocale = new Locale(server.getPreferredLanguage());
                    // String CommandeTitle = messageSource.getMessage("ntCommandeTitle", null, ServerLocale);
                    // String ClientCommandeTitle = messageSource.getMessage("ntCommandeTitle", null, userLocale);
                    // String messageForTheServer = messageSource.getMessage("ntComandeUpdate", null, ServerLocale) + basket.getBoard().getNumTable();
                    // String messageForTheUser = messageSource.getMessage("ntClientCommandeUpdate", null, userLocale);
                    List<String>parames= new ArrayList<>();
                    List<String> paramesClient = new ArrayList<>();
                    parames.add(0,String.valueOf(basket.getBoard().getNumTable()));
                    notificationService.createNotification("ntCommandeTitle", "ntComandeUpdate",parames, server);
                    notificationService.createNotification("ntCommandeTitle", "ntClientCommandeUpdate",paramesClient, user);
                }
            }
            return ResponseEntity.ok().body(productQuantities);
        }
        
        

    // with update of the quantity
    @PostMapping("/delete/one/product/{productId}/from_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','PROVIDER')")
    public ResponseEntity<?> deleteOneProductFromBasket(@PathVariable long basketId, @PathVariable long productId) {
        basketProductService.deleteProductFromBasket(basketId, productId);
        return ResponseEntity.ok().body("product deleted with success");
    }

    // delete the product from the basket what ever the quantity
    @PostMapping("/delete/product/{productId}/from_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','PROVIDER')")
    public ResponseEntity<?> deleteProductFromBasket(@PathVariable long basketId, @PathVariable long productId) {
        basketProductService.deleteProductFromBasketV2(basketId, productId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isProvider = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_PROVIDER"));
            if (isProvider) {
                Basket basket = basketService.findBasketById(basketId);
                Command command = basket.getCommand();
                command.setStatus(UPDATED);
                User user =command.getUser();
                Board board = basket.getBoard();
                User server = board.getZone().getServer();
                Locale userLocale = new Locale(user.getPreferredLanguage());
                Locale ServerLocale = new Locale(server.getPreferredLanguage());
                String CommandeTitle = messageSource.getMessage("ntCommandeTitle", null, ServerLocale);
                String ClientCommandeTitle = messageSource.getMessage("ntCommandeTitle", null, userLocale);
                String messageForTheServer = messageSource.getMessage("ntComandeUpdate", null, ServerLocale) + basket.getBoard().getNumTable();
                String messageForTheUser = messageSource.getMessage("ntClientCommandeUpdate", null, userLocale);
                // String messageForTheServer = "Commande updated: " + basket.getBoard().getNumTable();
                // String messageForTheUser = "Your command has been updated by manager successfully";
                List<String>parames= new ArrayList<>();
                List<String> paramesClient = new ArrayList<>();
                parames.add(0,String.valueOf(basket.getBoard().getNumTable()));
                notificationService.createNotification("ntCommandeTitle", "ntComandeUpdate",parames, server);
                notificationService.createNotification("ntCommandeTitle", "ntClientCommandeUpdate",paramesClient, user);
            }
        }
        return ResponseEntity.ok().body("product deleted with success");
    }

    @PostMapping("/{basketId}/commands")
    @PreAuthorize("hasAnyRole('GUEST','USER','PROVIDER')")
    public ResponseEntity<Command> createCommand(@PathVariable Long basketId) {
        Basket basket = basketService.findBasketById(basketId);
        Command command = commandService.createCommand(new Command());
        basketService.assignCommandToBasket(basketId, command);
        return ResponseEntity.ok(command);
    }

    // @PostMapping("/{basketId}/commands/add/user/{userId}")
    // @PreAuthorize("hasAnyRole('GUEST','USER')")
    // public ResponseEntity<Command> createCommandWithServer(@PathVariable Long basketId, @PathVariable long userId) {
    //     Basket basket = basketService.findBasketById(basketId);
        
    //     Board board = basket.getBoard();
    //     User user = userService.findUserById(userId);
    //     User server = board.getZone().getServer();

    //     if (server == null) {
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Or handle this case as per your requirements
    //     }

    //     Command command = commandService.createCommand(new Command());
    //     basketService.assignCommandToBasket(basketId, command);
    //     commandService.setServerForCommand(command.getIdCommand(), server);
    //     command.setUser(user);
    //     commandService.updateCommand(command);

    //     String messageForTheServer = "New command placed by the table number: " + command.getBasket().getBoard().getNumTable();
    //     String messageForTheUser = "Your command has been placed successfully";
    //     notificationService.createNotification("Command Notification", messageForTheServer, command.getServer());
    //     notificationService.createNotification("Command Notification", messageForTheUser, command.getUser());

    //     return ResponseEntity.ok(command);
    // }


    //Get products by id basket : done
    @GetMapping("/products/by_basket/{basketId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER','PROVIDER')")
    public ResponseEntity<?> getProductsByIdBasket(@PathVariable long basketId){
        Basket basket = basketService.findBasketById(basketId);
        if (basket == null){
            return ResponseEntity.badRequest().body("basket doesn't exist with this id");
        }
        Map<Product, QuantitysProduct> productQuantityMap = basketProductService.getProductsQuantityByBasketId(basketId);
        List<ProductQuantity> productQuantities = new ArrayList<>();
        for (Product p : productQuantityMap.keySet()){
            QuantitysProduct quantitiesProduct = productQuantityMap.get(p);
            ProductStatus status = determineProductStatus(basket,p);  // This method should determine the status
            productQuantities.add(new ProductQuantity(p, quantitiesProduct.getOldQuantity(), quantitiesProduct.getQuantity(), status));
        }
        return ResponseEntity.ok().body(productQuantities);
    }

    private ProductStatus determineProductStatus(Basket basket, Product product) {
        Optional<BasketProduct> basketProducts = basketProductService.findBasketProductByBasketAndProduct(basket, product);
        BasketProduct basketProduct = basketProducts.get(); 
        if (basketProduct != null) {
            return basketProduct.getStatus();
        } else {
            return ProductStatus.NEW; // Default or handle as needed
        }
    }
    

    @GetMapping("/latest/basket/by_table/{tableId}")
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER','PROVIDER')")
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
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER','PROVIDER')")
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
    @PreAuthorize("hasAnyRole('GUEST','USER','WAITER','PROVIDER')")
    public ResponseEntity<?> getProductsByIdCommand(@PathVariable long commandId) {
        Command command = commandService.findCommandById(commandId);
        if (command == null) {
            return ResponseEntity.badRequest().body("Command doesn't exist with this id");
        }
        
        Basket basket = command.getBasket();
        if (basket == null) {
            return ResponseEntity.badRequest().body("Basket doesn't exist with this id");
        }
        
        Map<Product, QuantitysProduct> productQuantityMap = basketProductService.getProductsQuantityByBasketId(basket.getId_basket());
        List<ProductQuantity> productQuantities = new ArrayList<>();
        
        for (Map.Entry<Product, QuantitysProduct> entry : productQuantityMap.entrySet()) {
            Product p = entry.getKey();
            QuantitysProduct quantitiesProduct = entry.getValue();
            ProductStatus status = determineProductStatus(basket,p); // Method to determine the status
            productQuantities.add(new ProductQuantity(p, quantitiesProduct.getOldQuantity(), quantitiesProduct.getQuantity(), status));
        }
        
        return ResponseEntity.ok().body(productQuantities);
    }
}