package oit.is.z3541.kaizi.janken.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import oit.is.z3541.kaizi.janken.model.Entry;
import oit.is.z3541.kaizi.janken.model.Janken;

@Controller
public class JankenController {

  @Autowired
  private Entry entry;

  // ログイン済みのユーザを、DIで共有しているエントリー一覧に追加する。
  @GetMapping("/janken")
  public String janken(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    this.entry.addUser(loginUser);
    model.addAttribute("entry", this.entry);
    model.addAttribute("login_user", loginUser);

    return "janken.html";
  }

  // リンクで選んだ手をモデルに渡し、対戦結果を画面に表示する。
  @GetMapping("/jankengame")
  public String play(@RequestParam String hand, Principal prin, ModelMap model) {
    try {
      Janken janken = new Janken(hand);
      model.addAttribute("janken", janken);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }
    String loginUser = prin.getName();
    this.entry.addUser(loginUser);
    model.addAttribute("entry", this.entry);
    model.addAttribute("login_user", loginUser);

    return "janken.html";
  }
}
