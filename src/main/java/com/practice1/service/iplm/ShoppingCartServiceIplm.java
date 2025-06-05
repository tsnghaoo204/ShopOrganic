package com.practice1.service.iplm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.practice1.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practice1.entities.CartItem;
import com.practice1.entities.SanPham;
import com.practice1.entities.ShoppingCart;
import com.practice1.entities.Customer;
import com.practice1.repository.CartItemRepository;
import com.practice1.repository.ShoppingCartRepository;
import com.practice1.service.ShoppingCartService;

import javax.persistence.EntityNotFoundException;

@Service
public class ShoppingCartServiceIplm implements ShoppingCartService{
	@Autowired
	ShoppingCartRepository cartRepo;
	@Autowired
	CartItemRepository itemRepo;
	@Autowired
	SanPhamRepository spRepo;


	@Override
	public ShoppingCart reduceItem(SanPham sp, Customer customer) {
		ShoppingCart cart = customer.getShoppingCart();
		Set<CartItem> cartItems = cart.getCartItem();
		CartItem cartItem = findCartItem(cartItems, sp.getId());

		// Giảm số lượng
		cartItem.setQuantity(cartItem.getQuantity() - 1);

		// Nếu số lượng bằng 0, xóa CartItem khỏi giỏ hàng
		if (cartItem.getQuantity() <= 0) {
			cartItems.remove(cartItem);
			itemRepo.delete(cartItem); // Xóa CartItem khỏi database
		} else {
			// Nếu không, cập nhật lại tổng giá cho sản phẩm
			cartItem.setTotalPrice(cartItem.getQuantity() * sp.getPrice());
			itemRepo.save(cartItem);
		}

		// Tính toán lại tổng số lượng và tổng giá
		int totalQuantity = totalItems(cartItems);
		double totalPrices = totalPrice(cartItems);

		// Cập nhật lại thông tin giỏ hàng
		cart.setTotalItems(totalQuantity);
		cart.setTotalPrices(totalPrices);
		cart.setCustomer(customer);

		return cartRepo.save(cart);
	}






	@Override
	public ShoppingCart addItem(SanPham sp, int quantity, Customer customer) {
		ShoppingCart cart = customer.getShoppingCart();
		if(cart==null) {
			cart = new ShoppingCart();
		}
		Set<CartItem> cartItems = cart.getCartItem();
		 CartItem cartItem = findCartItem(cartItems, sp.getId());
	    if(cartItems==null) {
	    	 cartItems = new HashSet<>();
	    	 cartItem = new CartItem();
	    	 cartItem.setSp(sp);
	    	 cartItem.setQuantity(quantity);
	    	 cartItem.setTotalPrice(quantity*sp.getPrice() - quantity*sp.getPrice()*(sp.getPercentSale()/100));
	    	 cartItem.setCart(cart);
	    	 cartItems.add(cartItem);
             itemRepo.save(cartItem);
	    }else{
	    	if(cartItem==null) {
                cartItem = new CartItem();
                cartItem.setSp(sp);
                cartItem.setTotalPrice(quantity * sp.getPrice() - quantity*sp.getPrice()*(sp.getPercentSale()/100));
                cartItem.setQuantity(quantity);
                cartItem.setCart(cart);
                cartItems.add(cartItem);
                itemRepo.save(cartItem);
	    	}
	    	else {
	    		cartItem.setQuantity(cartItem.getQuantity() + quantity);
	    		cartItem.setTotalPrice(cartItem.getTotalPrice() +
						(quantity*sp.getPrice() - quantity*sp.getPrice()*(sp.getPercentSale()/100)));
	    		itemRepo.save(cartItem);
	    	}
        }
	    cart.setCartItem(cartItems);
	    int totalQuantity = totalItems(cartItems);
	    double totalPrices = totalPrice(cartItems);
	    
	    cart.setTotalItems(totalQuantity);
	    cart.setTotalPrices(totalPrices);
	    cart.setCustomer(customer);
        return cartRepo.save(cart);
	}




	@Override
	public ShoppingCart updateItem(SanPham sp, int quantity, Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShoppingCart deleteItem(SanPham sp, Customer customer) {
		ShoppingCart cart = customer.getShoppingCart();
		Set<CartItem> cartItems = cart.getCartItem();
		CartItem cartItem = findCartItem(cartItems, sp.getId());
		cartItems.remove(cartItem);
		itemRepo.delete(cartItem);
		int totalQuantity = totalItems(cartItems);
	    double totalPrices = totalPrice(cartItems);
	    cart.setTotalItems(totalQuantity);
	    cart.setTotalPrices(totalPrices);
	    cart.setCustomer(customer);
        return cartRepo.save(cart);
		
	}

	@Override
	public ShoppingCart deleteItem(long productId) {
		// Tìm sản phẩm theo ID
		SanPham sp = spRepo.findById((int) productId)
				.orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại với ID: " + productId));

		// Tìm các CartItem chứa sản phẩm này
		List<CartItem> cartItems = (List<CartItem>) itemRepo.findById(productId);

		// Duyệt qua từng CartItem để xử lý
		for (CartItem cartItem : cartItems) {
			ShoppingCart cart = cartItem.getCart();

			// Xóa CartItem khỏi ShoppingCart
			cart.getCartItem().remove(cartItem);

			// Cập nhật số lượng và tổng giá của ShoppingCart
			int totalQuantity = totalItems(cart.getCartItem());
			double totalPrices = totalPrice(cart.getCartItem());
			cart.setTotalItems(totalQuantity);
			cart.setTotalPrices(totalPrices);

			// Lưu lại ShoppingCart
			cartRepo.save(cart);

			// Xóa CartItem
			itemRepo.delete(cartItem);
		}

		// Sau khi xóa, trả về null hoặc một đối tượng cần thiết
		return null;
	}



	private CartItem findCartItem(Set<CartItem> cartItems, int productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;

        for (CartItem item : cartItems) {
            if (item.getSp().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }
	private int totalItems(Set<CartItem> cartItems){
        int totalItems = 0;
        for(CartItem item : cartItems){
            totalItems += item.getQuantity();
        }
        return totalItems;
    }

    private double totalPrice(Set<CartItem> cartItems){
        double totalPrice = 0.0;

        for(CartItem item : cartItems){
            totalPrice += item.getTotalPrice();
        }

        return totalPrice;
    }

	@Override
	public List<CartItem> getAllItem() {
		
		return (List<CartItem>) itemRepo.findAll();
	}




}
