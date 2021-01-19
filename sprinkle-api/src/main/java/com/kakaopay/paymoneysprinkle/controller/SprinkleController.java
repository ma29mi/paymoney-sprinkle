package com.kakaopay.paymoneysprinkle.controller;

import com.kakaopay.paymoneysprinkle.consts.Constants.ResponseCode;
import com.kakaopay.paymoneysprinkle.consts.Constants.ResponseKey;
import com.kakaopay.paymoneysprinkle.domain.Response;
import com.kakaopay.paymoneysprinkle.domain.SprinkleEvent;
import com.kakaopay.paymoneysprinkle.service.SprinkleEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/sprinkle")
public class SprinkleController {

    @Autowired
    private SprinkleEventService sprinkleEventService;

    @PostMapping
    public ResponseEntity<Response> createSprinkleEvent(
            @RequestHeader(value = "X-USER-ID") long userId,
            @RequestHeader(value = "X-ROOM-ID") String roomId,
            @Valid @RequestBody SprinkleEvent sprinkleEvent) {

        sprinkleEvent.setUserId(userId);
        sprinkleEvent.setRoomId(roomId);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(ResponseKey.TOKEN, sprinkleEventService.sprinklePayMoney(sprinkleEvent));

        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS, null, resultMap));
    }

    @PatchMapping
    public ResponseEntity<Response> pickUpPayMoney(
            @RequestHeader(value = "X-USER-ID") long userId,
            @RequestHeader(value = "X-ROOM-ID") String roomId,
            @RequestHeader(value = "X-TOKEN") String token) {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(ResponseKey.AMOUNT, sprinkleEventService.pickUpPayMoney(userId, roomId, token));

        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS, resultMap));
    }

    @GetMapping
    public ResponseEntity<Response> viewSprinkleHistory(
            @RequestHeader(value = "X-USER-ID") long userId,
            @RequestHeader(value = "X-ROOM-ID") String roomId,
            @RequestHeader(value = "X-TOKEN") String token) {

        Map<String, Object> resultMap = sprinkleEventService.getSprinkleEventDetail(userId, roomId, token);

        return ResponseEntity.ok(new Response(ResponseCode.SUCCESS, resultMap));
    }
}
