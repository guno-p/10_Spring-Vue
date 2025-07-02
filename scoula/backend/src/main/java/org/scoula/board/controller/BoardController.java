package org.scoula.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.scoula.board.domain.BoardAttachmentVO;
import org.scoula.board.dto.BoardDTO;
import org.scoula.board.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import org.scoula.common.util.UploadFiles;

@Slf4j                           // 로깅을 위한 Lombok 어노테이션
@RestController                  // @Controller + @ResponseBody
@RequestMapping("/api/board")     // 기본 URL 패턴 설정
@RequiredArgsConstructor         // final 필드 생성자 자동 생성
public class BoardController {
  // 의존성 주입: BoardService를 통해 비즈니스 로직 처리
  final private BoardService service;

  @GetMapping("")
  public ResponseEntity<List<BoardDTO>> getList() {
    log.info("============> 게시글 전체 목록 조회");

    List<BoardDTO> list = service.getList();
    return ResponseEntity.ok(list); // 200 OK + 데이터 반환
    // +응답 헤더는 application/json (@RestController)
  }

  @GetMapping("/{no}")
  public ResponseEntity<BoardDTO> get(@PathVariable Long no) {
    log.info("============> 게시글 상세 조회: " + no);

    BoardDTO board = service.get(no);
    return ResponseEntity.ok(board);
  }

  @PostMapping("")
  public ResponseEntity<BoardDTO> create(BoardDTO board) {
    log.info("============> 게시글 생성: " + board);

    // 새 게시글 생성 후 결과 반환
    BoardDTO createdBoard = service.create(board);
    return ResponseEntity.ok(createdBoard);
  }

  @PutMapping("/{no}")
  public ResponseEntity<BoardDTO> update(
          @PathVariable Long no,           // URL에서 게시글 번호 추출
          BoardDTO board      // 수정할 데이터 (JSON)
  ) {
    log.info("============> 게시글 수정: " + no + ", " + board);

    // 게시글 번호 설정 (안전성을 위해)
    board.setNo(no);
    BoardDTO updatedBoard = service.update(board);
    return ResponseEntity.ok(updatedBoard);
  }

  @DeleteMapping("/{no}")
  public ResponseEntity<BoardDTO> delete(@PathVariable Long no) {
    log.info("============> 게시글 삭제: " + no);

    // 삭제된 게시글 정보를 반환
    BoardDTO deletedBoard = service.delete(no);
    return ResponseEntity.ok(deletedBoard);
  }

  @GetMapping("/download/{no}")
  public void download(@PathVariable Long no, HttpServletResponse response) throws Exception {
    BoardAttachmentVO attachment = service.getAttachment(no);
    File file = new File(attachment.getPath());
    UploadFiles.download(response, file, attachment.getFilename());
  }

  @DeleteMapping("/deleteAttachment/{no}")
  public ResponseEntity<Boolean> deleteAttachment(@PathVariable Long no) throws Exception {
    return ResponseEntity.ok(service.deleteAttachment(no));
  }
}