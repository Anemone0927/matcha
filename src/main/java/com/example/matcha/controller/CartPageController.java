package com.example.matcha.controller;

// 必要なインポート
import com.example.matcha.entity.Product;
import com.example.matcha.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // @RequestMappingを追加
import java.util.List;

@Controller
@RequestMapping("/cart") // /cart 以下にマッピング
public class CartPageController {

    // 💡 変更点1: @Autowired を削除し、final宣言にします
    private final ProductRepository productRepository;

    // 💡 変更点2: コンストラクタで ProductRepository を受け取ります
    public CartPageController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * カート追加フォーム画面を表示する (GET /cart/add)
     * 全商品リストをモデルに格納する
     */
    @GetMapping("/add") // /cart/add にマッピング
    public String showAddCartForm(Model model) {
        
        // データベースから全商品リストを取得
        List<Product> allProducts = productRepository.findAll();
        
        // モデルに商品リストを追加 (HTMLで使うため)
        model.addAttribute("allProducts", allProducts);
        
        return "add_cart"; // add_cart.html を表示する
    }
    
    // 💡 /cart_list のエンドポイントは CartController で処理されるため、ここから削除しました。
}