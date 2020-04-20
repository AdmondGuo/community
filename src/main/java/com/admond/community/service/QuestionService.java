package com.admond.community.service;

import com.admond.community.dto.QuestionDTO;
import com.admond.community.mapper.QuestionMapper;
import com.admond.community.mapper.UserMapper;
import com.admond.community.model.Question;
import com.admond.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private  QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //方法：以列表形式返回所有的QuestionDTO(Question + User)
    public List<QuestionDTO> list(Integer page, Integer size) {
        List<Question> questions = questionMapper.list();
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
           User user =  userMapper.findByID(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);  //将前一个对象的属性拷贝到第二个对象中
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }
}
