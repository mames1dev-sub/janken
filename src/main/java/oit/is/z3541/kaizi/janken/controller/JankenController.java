package oit.is.z3541.kaizi.janken.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import oit.is.z3541.kaizi.janken.model.Janken;
import oit.is.z3541.kaizi.janken.model.Match;
import oit.is.z3541.kaizi.janken.model.MatchMapper;
import oit.is.z3541.kaizi.janken.model.User;
import oit.is.z3541.kaizi.janken.model.UserMapper;

@Controller
public class JankenController {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private MatchMapper matchMapper;

  @GetMapping("/janken")
  public String janken(Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    ArrayList<User> users = userMapper.selectAllUsers();
    model.addAttribute("users", users);
    ArrayList<Match> matches = matchMapper.selectAllMatches();
    model.addAttribute("matches", matches);
    model.addAttribute("login_user", loginUser);

    return "janken.html";
  }

  @GetMapping("/match")
  public String match(@RequestParam Integer id, Principal prin, ModelMap model) {
    User opponent = userMapper.selectById(id);
    if (opponent == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザが見つかりません。");
    }
    String loginUser = prin.getName();
    model.addAttribute("login_user", loginUser);
    model.addAttribute("opponent", opponent);

    return "match.html";
  }

  @GetMapping("/fight")
  @Transactional
  public String fight(@RequestParam Integer id, @RequestParam String hand, Principal prin, ModelMap model) {
    String loginUser = prin.getName();
    User user = userMapper.selectByName(loginUser);
    User opponent = userMapper.selectById(id);
    if (user == null || opponent == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザが見つかりません。");
    }
    if (!"CPU".equals(opponent.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "対戦相手はCPUを選択してください。");
    }

    Janken janken;
    try {
      janken = new Janken(hand);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    Match match = new Match();
    match.setUser1(user.getId());
    match.setUser2(opponent.getId());
    match.setUser1Hand(hand);
    match.setUser2Hand("Gu");
    matchMapper.insertMatch(match);

    model.addAttribute("login_user", loginUser);
    model.addAttribute("opponent", opponent);
    model.addAttribute("janken", janken);
    return "match.html";
  }

  @GetMapping("/jankengame")
  public String play(@RequestParam String hand, Principal prin, ModelMap model) {
    try {
      Janken janken = new Janken(hand);
      model.addAttribute("janken", janken);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }
    String loginUser = prin.getName();
    ArrayList<User> users = userMapper.selectAllUsers();
    model.addAttribute("users", users);
    ArrayList<Match> matches = matchMapper.selectAllMatches();
    model.addAttribute("matches", matches);
    model.addAttribute("login_user", loginUser);

    return "janken.html";
  }
}
