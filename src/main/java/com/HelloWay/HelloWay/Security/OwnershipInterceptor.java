package com.HelloWay.HelloWay.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Contrôle d'appartenance centralisé : chaque variable de chemin connue
 * (spaceId, zoneId, productId, {id} selon le contrôleur...) est rattachée
 * à son espace et comparée à l'espace du gérant / serveur connecté.
 * Réponse 403 si la ressource appartient à un autre espace.
 */
@Component
public class OwnershipInterceptor implements HandlerInterceptor {

    @Autowired
    private AccessGuard guard;

    private static final Map<String, BiPredicate<AccessGuard, Long>> BY_NAME = new HashMap<>();
    private static final Map<String, BiPredicate<AccessGuard, Long>> BY_CONTROLLER = new HashMap<>();

    static {
        BiPredicate<AccessGuard, Long> space = AccessGuard::space;
        BiPredicate<AccessGuard, Long> zone = AccessGuard::zone;
        BiPredicate<AccessGuard, Long> board = AccessGuard::board;
        BiPredicate<AccessGuard, Long> category = AccessGuard::category;
        BiPredicate<AccessGuard, Long> product = AccessGuard::product;

        BY_NAME.put("spaceId", space);
        BY_NAME.put("id_space", space);
        BY_NAME.put("idSpace", space);
        BY_NAME.put("zoneId", zone);
        BY_NAME.put("id_zone", zone);
        BY_NAME.put("boardId", board);
        BY_NAME.put("categorieId", category);
        BY_NAME.put("id_categorie", category);
        BY_NAME.put("productId", product);
        BY_NAME.put("id_product", product);
        BY_NAME.put("idProduct", product);
        BY_NAME.put("primaryMaterialId", AccessGuard::material);
        BY_NAME.put("eventId", AccessGuard::event);
        BY_NAME.put("idEvent", AccessGuard::event);
        BY_NAME.put("reservationId", AccessGuard::reservation);
        BY_NAME.put("basketId", AccessGuard::basket);
        BY_NAME.put("id_basket", AccessGuard::basket);
        BY_NAME.put("commandId", AccessGuard::command);
        BY_NAME.put("serverId", AccessGuard::staffUser);
        BY_NAME.put("moderatorUserId", AccessGuard::self);
        BY_NAME.put("idModerator", AccessGuard::self);

        // {id} / noms ambigus : dépend du contrôleur
        BY_CONTROLLER.put("SpaceController:id", space);
        BY_CONTROLLER.put("ZoneController:id", zone);
        BY_CONTROLLER.put("BoardController:id", board);
        BY_CONTROLLER.put("CategorieController:id", category);
        BY_CONTROLLER.put("ProductController:id", product);
        BY_CONTROLLER.put("ProductController:idSpace", product); // DELETE {idImage}/images/{idSpace} = id produit
        BY_CONTROLLER.put("EventController:id", AccessGuard::event);
        BY_CONTROLLER.put("PrimaryMaterialController:id", AccessGuard::material);
        BY_CONTROLLER.put("PrimaryMaterialHistoryController:id", AccessGuard::materialHistory);
        BY_CONTROLLER.put("ReservationController:id", AccessGuard::reservation);
        BY_CONTROLLER.put("BasketController:id", AccessGuard::basket);
        BY_CONTROLLER.put("WifiController:id", AccessGuard::wifi);
        BY_CONTROLLER.put("ShiftController:shiftId", AccessGuard::shift);
        BY_CONTROLLER.put("ShiftSystemController:shiftId", AccessGuard::shiftSystem);
        BY_CONTROLLER.put("UserController:id", AccessGuard::staffUser);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod) || !guard.isStaffToCheck()) {
            return true;
        }
        @SuppressWarnings("unchecked")
        Map<String, String> vars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (vars == null || vars.isEmpty()) {
            return true;
        }
        String controller = ((HandlerMethod) handler).getBeanType().getSimpleName();
        boolean isRead = "GET".equalsIgnoreCase(request.getMethod());

        for (Map.Entry<String, String> var : vars.entrySet()) {
            String key = controller + ":" + var.getKey();
            // Les lectures de profils (UserController GET /id/{id}) restent permises au personnel
            if (isRead && key.equals("UserController:id")) continue;

            BiPredicate<AccessGuard, Long> check = BY_CONTROLLER.getOrDefault(key, BY_NAME.get(var.getKey()));
            if (check == null) continue;

            Long id;
            try {
                id = Long.parseLong(var.getValue());
            } catch (NumberFormatException e) {
                continue;
            }
            if (!check.test(guard, id)) {
                response.sendError(HttpStatus.FORBIDDEN.value(), "Cette ressource appartient à un autre espace.");
                return false;
            }
        }
        return true;
    }
}
