package org.scoula.board.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.scoula.board.domain.BoardAttachmentVO;
import org.scoula.board.dto.BoardDTO;
import org.scoula.board.service.BoardService;
import org.scoula.common.pagination.Page;
import org.scoula.common.pagination.PageRequest;
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


  /**
   * 페이징된 게시글 목록 조회
   * GET: http://localhost:8080/api/board?page=1&amount=10
   * @param pageRequest 쿼리스트링이 자동 바인딩된 커맨드 객체
   * @return ResponseEntity
   *         - 200 OK: 목록 조회 성공, 페이징 처리된 게시글 리스트 반환 (빈 리스트 포함)
   *         - 204 No Content: 조회 성공했지만 게시글이 하나도 없음
   *         - 500 Internal Server Error: 서버 내부 오류 (DB 연결 실패 등)
   */
  @ApiOperation(value = "게시글 목록 조회(Pagination)", notes = "페이징 처리된 게시글 목록을 얻는 API")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = BoardDTO.class),
          @ApiResponse(code = 400, message = "잘못된 요청입니다."),
          @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
  })
  @GetMapping("")
  public ResponseEntity<Page> getList(
          @ApiParam(value = "페이지네이션 요청 객체", required = true) PageRequest pageRequest) {
    Page<BoardDTO> result = service.getPage(pageRequest);
    return ResponseEntity.ok(result);
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