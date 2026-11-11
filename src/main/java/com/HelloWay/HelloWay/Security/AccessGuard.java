package com.HelloWay.HelloWay.Security;

import com.HelloWay.HelloWay.entities.*;
import com.HelloWay.HelloWay.repos.*;
import com.HelloWay.HelloWay.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Vérifie qu'un gérant (PROVIDER) ou un serveur (WAITER) ne touche qu'aux
 * données de son propre espace. L'ADMIN passe toujours ; les clients
 * (USER / GUEST) ne sont pas concernés ici.
 * Utilisable dans @PreAuthorize : @guard.zone(#id), @guard.space(#spaceId)...
 */
@Component("guard")
public class AccessGuard {

    @Autowired private UserRepository userRepository;
    @Autowired private SpaceRepository spaceRepository;
    @Autowired private ZoneRepository zoneRepository;
    @Autowired private BoardRepository boardRepository;
    @Autowired private CategorieRepository categorieRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private PrimaryMaterialRepository primaryMaterialRepository;
    @Autowired private PrimaryMaterialHistoryRepository primaryMaterialHistoryRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private BasketRepository basketRepository;
    @Autowired private CommandRepository commandRepository;
    @Autowired private WifiRepository wifiRepository;
    @Autowired private ShiftRepository shiftRepository;
    @Autowired private ShiftSystemRepository shiftSystemRepository;

    // ---------- utilisateur courant ----------

    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }

    public Set<String> currentRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Set.of();
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public boolean isAdmin() {
        return currentRoles().contains("ROLE_ADMIN");
    }

    /** Vrai si l'appelant est du personnel d'un espace (gérant ou serveur) et pas admin. */
    public boolean isStaffToCheck() {
        Set<String> roles = currentRoles();
        return !roles.contains("ROLE_ADMIN")
                && (roles.contains("ROLE_PROVIDER") || roles.contains("ROLE_WAITER"));
    }

    /** Espace du personnel connecté : espace géré (gérant) ou espace de travail (serveur). */
    public Long currentStaffSpaceId() {
        Long userId = currentUserId();
        if (userId == null) return null;
        User me = userRepository.findById(userId).orElse(null);
        if (me == null) return null;
        if (me.getModeratorSpace() != null) return me.getModeratorSpace().getId_space();
        if (me.getServersSpace() != null) return me.getServersSpace().getId_space();
        return null;
    }

    /** null = ressource introuvable (le contrôleur répondra 404). */
    private boolean allowed(Long resourceSpaceId, boolean found) {
        if (!isStaffToCheck() || !found) return true;
        Long mine = currentStaffSpaceId();
        return mine != null && Objects.equals(mine, resourceSpaceId);
    }

    // ---------- résolution ressource -> espace ----------

    private static Long spaceOf(Space s) { return s == null ? null : s.getId_space(); }
    private static Long spaceOf(Zone z) { return z == null ? null : spaceOf(z.getSpace()); }
    private static Long spaceOf(Board b) { return b == null ? null : spaceOf(b.getZone()); }
    private static Long spaceOf(Categorie c) { return c == null ? null : spaceOf(c.getSpace()); }
    private static Long spaceOf(Product p) { return p == null ? null : spaceOf(p.getCategorie()); }
    private static Long spaceOf(PrimaryMaterial m) { return m == null ? null : spaceOf(m.getSpace()); }

    private static Long spaceOfUser(User u) {
        if (u == null) return null;
        if (u.getModeratorSpace() != null) return u.getModeratorSpace().getId_space();
        if (u.getServersSpace() != null) return u.getServersSpace().getId_space();
        return null;
    }

    public boolean space(Long id) {
        return id == null || allowed(id, spaceRepository.existsById(id));
    }

    public boolean zone(Long id) {
        if (id == null) return true;
        Optional<Zone> z = zoneRepository.findById(id);
        return allowed(z.map(AccessGuard::spaceOf).orElse(null), z.isPresent());
    }

    public boolean board(Long id) {
        if (id == null) return true;
        Optional<Board> b = boardRepository.findById(id);
        return allowed(b.map(AccessGuard::spaceOf).orElse(null), b.isPresent());
    }

    public boolean category(Long id) {
        if (id == null) return true;
        Optional<Categorie> c = categorieRepository.findById(id);
        return allowed(c.map(AccessGuard::spaceOf).orElse(null), c.isPresent());
    }

    public boolean product(Long id) {
        if (id == null) return true;
        Optional<Product> p = productRepository.findById(id);
        return allowed(p.map(AccessGuard::spaceOf).orElse(null), p.isPresent());
    }

    public boolean event(Long id) {
        if (id == null) return true;
        Optional<Event> e = eventRepository.findById(id);
        return allowed(e.map(ev -> spaceOf(ev.getSpace())).orElse(null), e.isPresent());
    }

    public boolean material(Long id) {
        if (id == null) return true;
        Optional<PrimaryMaterial> m = primaryMaterialRepository.findById(id);
        return allowed(m.map(AccessGuard::spaceOf).orElse(null), m.isPresent());
    }

    public boolean materialHistory(Long id) {
        if (id == null) return true;
        Optional<PrimaryMaterialHistory> h = primaryMaterialHistoryRepository.findById(id);
        return allowed(h.map(x -> spaceOf(x.getPrimaryMaterial())).orElse(null), h.isPresent());
    }

    public boolean reservation(Long id) {
        if (id == null) return true;
        Optional<Reservation> r = reservationRepository.findById(id);
        return allowed(r.map(x -> spaceOf(x.getSpace())).orElse(null), r.isPresent());
    }

    public boolean basket(Long id) {
        if (id == null) return true;
        Optional<Basket> b = basketRepository.findById(id);
        return allowed(b.map(x -> spaceOf(x.getBoard())).orElse(null), b.isPresent());
    }

    public boolean command(Long id) {
        if (id == null) return true;
        Optional<Command> c = commandRepository.findById(id);
        return allowed(c.map(x -> x.getSpace() != null ? spaceOf(x.getSpace())
                : x.getBasket() != null ? spaceOf(x.getBasket().getBoard()) : null).orElse(null), c.isPresent());
    }

    public boolean wifi(Long id) {
        if (id == null) return true;
        Optional<Wifi> w = wifiRepository.findById(id);
        return allowed(w.map(x -> spaceOf(x.getSpace())).orElse(null), w.isPresent());
    }

    public boolean shift(Long id) {
        if (id == null) return true;
        Optional<Shift> s = shiftRepository.findById(id);
        return allowed(s.map(x -> spaceOfUser(x.getWaiter())).orElse(null), s.isPresent());
    }

    public boolean shiftSystem(Long id) {
        if (id == null) return true;
        Optional<ShiftSystem> s = shiftSystemRepository.findById(id);
        return allowed(s.map(x -> spaceOfUser(x.getWaiter())).orElse(null), s.isPresent());
    }

    /** Le personnel ne peut agir que sur lui-même ou (gérant) sur un serveur de son espace. */
    public boolean staffUser(Long targetUserId) {
        if (targetUserId == null || !isStaffToCheck()) return true;
        if (Objects.equals(targetUserId, currentUserId())) return true;
        if (!currentRoles().contains("ROLE_PROVIDER")) return false;
        Optional<User> target = userRepository.findById(targetUserId);
        if (target.isEmpty()) return true;
        User t = target.get();
        Long mine = currentStaffSpaceId();
        return mine != null && t.getServersSpace() != null
                && Objects.equals(mine, t.getServersSpace().getId_space());
    }

    /**
     * Modifier un compte : admin -> tous ; soi-même ; gérant -> ses serveurs.
     * Les clients ne peuvent modifier que leur propre compte.
     */
    public boolean canEditUser(Long targetUserId) {
        if (targetUserId == null) return false;
        if (isAdmin() || Objects.equals(targetUserId, currentUserId())) return true;
        return currentRoles().contains("ROLE_PROVIDER") && staffUser(targetUserId);
    }

    /** L'identifiant fourni doit être celui de l'appelant (sauf admin). */
    public boolean self(Long userId) {
        return userId == null || isAdmin() || Objects.equals(userId, currentUserId());
    }
}
