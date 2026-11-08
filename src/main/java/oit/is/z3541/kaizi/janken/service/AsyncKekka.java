package oit.is.z3541.kaizi.janken.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import oit.is.z3541.kaizi.janken.model.Match;
import oit.is.z3541.kaizi.janken.model.MatchMapper;

@Service
public class AsyncKekka {
  private final Logger logger = LoggerFactory.getLogger(AsyncKekka.class);

  @Autowired
  private MatchMapper matchMapper;

  @Async
  public void asyncShowResult(SseEmitter emitter, int user1, int user2, int afterId) {
    AtomicBoolean connected = new AtomicBoolean(true);
    emitter.onCompletion(() -> connected.set(false));
    emitter.onTimeout(() -> connected.set(false));
    emitter.onError(error -> connected.set(false));

    try {
      while (connected.get()) {
        Match match = matchMapper.selectActiveResult(user1, user2, afterId);
        if (match == null) {
          TimeUnit.MILLISECONDS.sleep(500);
          continue;
        }

        emitter.send(match);
        TimeUnit.SECONDS.sleep(1);
        match.setIsActive(false);
        matchMapper.updateById(match);
        break;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (IOException e) {
      logger.warn("SSE error: {}", e.toString());
      return;
    } catch (Exception e) {
      logger.warn("SSE error: {}", e.toString());
    }
    emitter.complete();
  }
}
