package ru.study.officeops.web;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.study.officeops.domain.*;
import ru.study.officeops.security.AccessGuard;
import ru.study.officeops.service.*;

@Controller
public class PagesController {
    private final AdminService admin;
    private final TicketService tickets;
    private final AccessGuard guard;
    public PagesController(AdminService admin, TicketService tickets, AccessGuard guard) {
        this.admin = admin; this.tickets = tickets; this.guard = guard;
    }
    @ModelAttribute void navigation(Model model) {
        model.addAttribute("canAssets", guard.can("ASSET_READ"));
        model.addAttribute("canTickets", guard.can("TICKET_READ"));
        model.addAttribute("canAccess", guard.can("ACCESS_MANAGE"));
        model.addAttribute("canAudit", guard.can("AUDIT_READ"));
        model.addAttribute("actor", guard.can("ASSET_READ") || guard.can("TICKET_READ") || guard.can("ACCESS_MANAGE") || guard.can("AUDIT_READ") ? guard.actor() : "");
    }
    @GetMapping("/login") String login() { return "login"; }
    @RequestMapping(value = "/denied", method = {RequestMethod.GET, RequestMethod.POST}) String denied() { return "denied"; }
    @GetMapping("/") String home() { return "home"; }
    @GetMapping("/assets") String assets(Model model) { model.addAttribute("items", admin.assets()); model.addAttribute("canWrite", guard.can("ASSET_WRITE")); return "assets"; }
    @PostMapping("/assets") String addAsset(@RequestParam String inventoryNumber, @RequestParam String name, @RequestParam String location, RedirectAttributes flash) {
        admin.addAsset(inventoryNumber, name, location); flash.addFlashAttribute("notice", "Оборудование добавлено"); return "redirect:/assets";
    }
    @GetMapping("/tickets") String tickets(Model model) {
        model.addAttribute("items", tickets.list()); model.addAttribute("canCreate", guard.can("TICKET_CREATE"));
        if (guard.can("TICKET_CREATE")) model.addAttribute("assets", admin.assets());
        return "tickets";
    }
    @PostMapping("/tickets") String addTicket(@RequestParam String subject, @RequestParam String description, @RequestParam Long assetId, RedirectAttributes flash) {
        tickets.create(subject, description, assetId); flash.addFlashAttribute("notice", "Заявка создана"); return "redirect:/tickets";
    }
    @GetMapping("/tickets/{id}") String ticket(@PathVariable Long id, Model model) {
        Ticket ticket = tickets.get(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("commands", tickets.available(ticket));
        model.addAttribute("canAssign", guard.can("TICKET_ASSIGN") && ticket.getStatus() == TicketStatus.NEW);
        if (guard.can("TICKET_ASSIGN")) model.addAttribute("operators", admin.availableAssignees());
        return "ticket";
    }
    @PostMapping("/tickets/{id}/assign") String assign(@PathVariable Long id, @RequestParam Long operatorId, RedirectAttributes flash) {
        tickets.assign(id, operatorId); flash.addFlashAttribute("notice", "Исполнитель назначен"); return "redirect:/tickets/" + id;
    }
    @PostMapping("/tickets/{id}/commands/{code}") String command(@PathVariable Long id, @PathVariable String code, RedirectAttributes flash) {
        tickets.run(id, code); flash.addFlashAttribute("notice", "Статус обновлён"); return "redirect:/tickets/" + id;
    }
    @GetMapping("/access") String access(Model model) {
        model.addAttribute("roles", admin.roles()); model.addAttribute("operators", admin.operators());
        model.addAttribute("permissions", Permission.values()); return "access";
    }
    @PostMapping("/access/roles/{id}") String role(@PathVariable Long id, @RequestParam(required = false) List<String> permissions, RedirectAttributes flash) {
        Set<Permission> selected = EnumSet.noneOf(Permission.class);
        if (permissions != null) for (String value : permissions) selected.add(Permission.valueOf(value));
        admin.updatePermissions(id, selected); flash.addFlashAttribute("notice", "Права роли сохранены"); return "redirect:/access";
    }
    @PostMapping("/access/operators") String operator(@RequestParam String username, @RequestParam String displayName,
            @RequestParam String password, @RequestParam Long roleId, RedirectAttributes flash) {
        admin.addOperator(username, displayName, password, roleId); flash.addFlashAttribute("notice", "Оператор создан"); return "redirect:/access";
    }
    @PostMapping("/access/operators/{id}") String changeOperator(@PathVariable Long id, @RequestParam Long roleId,
            @RequestParam(defaultValue = "false") boolean enabled, RedirectAttributes flash) {
        admin.changeOperator(id, roleId, enabled); flash.addFlashAttribute("notice", "Оператор обновлён"); return "redirect:/access";
    }
    @GetMapping("/audit") String audit(Model model) { model.addAttribute("items", admin.audit()); return "audit"; }
    @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRequest(IllegalArgumentException ex, Model model) { model.addAttribute("message", ex.getMessage()); return "problem"; }
}
