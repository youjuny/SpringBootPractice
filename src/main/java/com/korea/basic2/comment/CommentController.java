package com.korea.basic2.comment;


import com.korea.basic2.answer.Answer;
import com.korea.basic2.answer.AnswerService;
import com.korea.basic2.question.Question;
import com.korea.basic2.question.QuestionService;
import com.korea.basic2.user.SiteUser;
import com.korea.basic2.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;


@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/create/question/{id}")
    public String createQuestionComment(CommentForm commentForm) {
        return "comment_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/question/{id}")
    public String createQuestionComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
                                        BindingResult bindingResult, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser user = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        Comment comment = this.commentService.create(question, commentForm.getContent(), user);
        return String.format("redirect:/question/detail/%s#comment_%s", comment.getQuestion().getId(), comment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value ="/create/answer/{id}")
    public String createAnswerComment(CommentForm commentForm) {
        return "comment_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/answer/{id}")
    public String createAnswerComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
                                        BindingResult bindingResult, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser user = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answer);
            return "question_detail";
        }

        Comment comment = this.commentService.create(answer, commentForm.getContent(), user);
        return String.format("redirect:/question/detail/%s#comment_%s", comment.getAnswer().getId(), comment.getId());
    }

    // 여기서 막힌다.... url은 제대로 이동하는데 왜 404 오류가 나는지 모르겠다.






    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyComment(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal) {
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            if (!c.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            commentForm.setContent(c.getContent());
        }
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyComment(@Valid CommentForm commentForm, BindingResult bindingResult, Principal principal,
                                @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            if (!c.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            c = this.commentService.modify(c, commentForm.getContent());
            return String.format("redirect:/question/detail/%s", c.getQuestionId());

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(Principal principal, @PathVariable("id") Integer id) {
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            if (!c.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
            this.commentService.delete(c);
            return String.format("redirect:/question/detail/%s", c.getQuestionId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }

}
