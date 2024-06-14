package com.sanryoo.shopping.feature.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.presentation.authentication.login.LogInScreen
import com.sanryoo.shopping.feature.presentation.authentication.signup.SignUpScreen
import com.sanryoo.shopping.feature.presentation.using.cart.CartScreen
import com.sanryoo.shopping.feature.presentation.using.change_password.ChangePasswordScreen
import com.sanryoo.shopping.feature.presentation.using.chats.ChatsScreen
import com.sanryoo.shopping.feature.presentation.using.checkout.CheckOutScreen
import com.sanryoo.shopping.feature.presentation.using.edit_product.EditProductScreen
import com.sanryoo.shopping.feature.presentation.using.home.HomeScreen
import com.sanryoo.shopping.feature.presentation.using.message.MessageScreen
import com.sanryoo.shopping.feature.presentation.using.my_account.UserScreen
import com.sanryoo.shopping.feature.presentation.using.my_likes.MyLikesScreen
import com.sanryoo.shopping.feature.presentation.using.my_purchase.MyPurchaseScreen
import com.sanryoo.shopping.feature.presentation.using.my_shop.MyShopScreen
import com.sanryoo.shopping.feature.presentation.using.my_shop_purchases.MyShopPurchaseScreen
import com.sanryoo.shopping.feature.presentation.using.notifications.NotificationScreen
import com.sanryoo.shopping.feature.presentation.using.product.ProductScreen
import com.sanryoo.shopping.feature.presentation.using.profile.ProfileScreen
import com.sanryoo.shopping.feature.presentation.using.review.ReviewScreen
import com.sanryoo.shopping.feature.presentation.using.shop.ShopScreen
import com.sanryoo.shopping.feature.util.Screen
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ShoppingNavGraph(
    navController: NavHostController,
    scaffoldState: ScaffoldState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        //Using
        composable(route = Screen.Home.route) {
            HomeScreen(navController, scaffoldState)
        }

        composable(route = Screen.Notification.route) {
            NotificationScreen(navController)
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(route = Screen.User.route) {
            UserScreen(navController)
        }

        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(navController, scaffoldState)
        }

        composable(route = Screen.MyShop.route) {
            MyShopScreen(navController, scaffoldState)
        }

        composable(route = Screen.MyShopPurchases.route) {
            MyShopPurchaseScreen(navController)
        }

        composable(route = Screen.MyLikes.route) {
            MyLikesScreen(navController)
        }

        composable(
            route = Screen.EditProduct.route + "?product={product}&label={label}&button={button}",
            arguments = listOf(
                navArgument("product") { type = NavType.StringType },
                navArgument("label") { type = NavType.StringType },
                navArgument("button") { type = NavType.StringType }
            )
        ) {
            val productJson = it.arguments?.getString("product") ?: ""
            val product = try {
                Gson().fromJson(productJson, Product::class.java) ?: Product()
            } catch (e: Exception) {
                Product()
            }
            val label = it.arguments?.getString("label") ?: "Add Product"
            val button = it.arguments?.getString("button") ?: "Publish"
            EditProductScreen(product, label, button, navController, scaffoldState)
        }

        composable(
            route = Screen.Product.route + "?product={product}",
            arguments = listOf(navArgument("product") { type = NavType.StringType })
        ) {
            val productJson = it.arguments?.getString("product") ?: ""
            val product = try {
                Gson().fromJson(productJson, Product::class.java) ?: Product()
            } catch (e: Exception) {
                Product()
            }
            ProductScreen(product, scaffoldState, navController)
        }

        composable(
            route = Screen.Shop.route + "?uid={uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) {
            val uid = it.arguments?.getString("uid") ?: ""
            ShopScreen(uid, navController)
        }

        composable(route = Screen.Cart.route) {
            CartScreen(navController)
        }

        composable(route = Screen.Chats.route) {
            ChatsScreen(navController)
        }

        composable(
            route = Screen.Message.route + "?othersId={othersId}",
            arguments = listOf(
                navArgument("othersId") { type = NavType.StringType }
            )
        ) {
            val othersId = it.arguments?.getString("othersId") ?: ""
            MessageScreen(othersId, navController)
        }

        composable(
            route = Screen.CheckOut.route + "?orders={orders}",
            arguments = listOf(navArgument("orders") { type = NavType.StringType })
        ) {
            val ordersString = it.arguments?.getString("orders")
            val orders = try {
                val listType = object : TypeToken<List<Order>>() {}.type
                Gson().fromJson(ordersString, listType)
            } catch (e: Exception) {
                emptyList<Order>()
            }
            CheckOutScreen(orders, navController, scaffoldState)
        }

        composable(route = Screen.MyPurchase.route) {
            MyPurchaseScreen(navController)
        }

        composable(
            route = Screen.Review.route + "?order={order}",
            arguments = listOf(navArgument("order") { type = NavType.StringType })
        ) {
            val orderJson = it.arguments?.getString("order") ?: ""
            val order = try {
                Gson().fromJson(orderJson, Order::class.java) ?: Order()
            } catch (e: Exception) {
                Order()
            }
            ReviewScreen(order, navController, scaffoldState)
        }

        //Authentication
        composable(route = Screen.LogIn.route) {
            LogInScreen(navController, scaffoldState)
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(navController, scaffoldState)
        }
    }
}