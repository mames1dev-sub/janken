package oit.is.z3541.kaizi.janken.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import oit.is.z3541.kaizi.janken.model.Janken;

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

  // リンクで選んだ手をモデルに渡し、対戦結果を画面に表示する。
  @GetMapping("/jankengame")
  public String play(@RequestParam String hand, ModelMap model) {
    try {
      Janken janken = new Janken(hand);
      model.addAttribute("janken", janken);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }
    return "janken.html";
  }
}
