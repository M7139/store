package com.ga.store.controller;

import com.ga.store.dto.CartItemQuantityRequest;
import com.ga.store.dto.CartItemRequest;
import com.ga.store.dto.CartResponse;
import com.ga.store.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(
        name = "Cart",
        description = "Customer shopping cart management"
)
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    public CartController(
            CartService cartService) {

        this.cartService = cartService;
    }

    @GetMapping
    @Operation(
            summary = "Get cart",
            description = "Returns the shopping cart of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        String email =
                authentication.getName();

        CartResponse response =
                cartService.getCart(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping("/items")
    @Operation(
            summary = "Add product to cart",
            description = "Adds a product and quantity to the current user's shopping cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product added to cart successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid cart item information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product is inactive or requested quantity exceeds available stock"
            )
    })
    public ResponseEntity<CartResponse> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {

        String email =
                authentication.getName();

        cartService.addItemToCart(
                email,
                request
        );

        CartResponse response =
                cartService.getCart(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/items/{cartItemId}")
    @Operation(
            summary = "Update cart item quantity",
            description = "Changes the quantity of an item in the current user's cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart item quantity updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid quantity"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Requested quantity exceeds available stock"
            )
    })
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            Authentication authentication,

            @Parameter(
                    description = "Cart item ID",
                    example = "1"
            )
            @PathVariable Long cartItemId,

            @Valid @RequestBody CartItemQuantityRequest request) {

        String email =
                authentication.getName();

        cartService.updateCartItemQuantity(
                email,
                cartItemId,
                request
        );

        CartResponse response =
                cartService.getCart(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(
            summary = "Remove cart item",
            description = "Removes a specific item from the current user's cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart item removed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found"
            )
    })
    public ResponseEntity<CartResponse> removeCartItem(
            Authentication authentication,

            @Parameter(
                    description = "Cart item ID",
                    example = "1"
            )
            @PathVariable Long cartItemId) {

        String email =
                authentication.getName();

        cartService.removeCartItem(
                email,
                cartItemId
        );

        CartResponse response =
                cartService.getCart(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping
    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the current user's shopping cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart cleared successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<String> clearCart(
            Authentication authentication) {

        String email =
                authentication.getName();

        cartService.clearCart(
                email
        );

        return new ResponseEntity<>(
                "Cart cleared successfully",
                HttpStatus.OK
        );
    }
}