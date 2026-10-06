package oit.is.z3541.kaizi.janken.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JankenController {

  // URLを直接開いた場合は、名前を渡さずに画面を表示する。
  @GetMapping("/janken")
  public String janken() {
    return "janken.html";
  }

  // 参加フォームから届いた名前を、今回表示する画面に渡す。
  @PostMapping("/janken")
  public String join(@RequestParam String userName, ModelMap model) {
    model.addAttribute("userName", userName);
    return "janken.html";
  }
}
