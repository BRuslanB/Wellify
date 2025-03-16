package kz.bars.wellify.admin_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.bars.wellify.admin_service.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Tag(name = "Item API", description = "API for managing items")
public class ItemController {

    @GetMapping(value = "/home")
    @PreAuthorize("isAuthenticated")
    @Operation(summary = "Home page")
    public String homePage(){
        return "This is home page " + JWTUtils.getCurrentUserName();
    }

    @GetMapping(value = "/about")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "About page")
    public String aboutPage(){
        return "This is about page " + JWTUtils.getCurrentUserFirstName() + " - " + JWTUtils.getCurrentUserLastName();
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin panel")
    public String adminPage(){
        return "This is admin page " + JWTUtils.getCurrentUserEmail();
    }
}
