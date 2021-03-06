package io.swagger.pojo.dao;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 功能描述：试卷实体类
 *
 * @作者：黄宽波
 * @时间:2019-07-09
 */
@Entity
@Table(name = "paper")
@Data
@Accessors(chain = true)
public class Paper extends BasicEntity {

    /**
     * 试卷id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 试卷标题
     */
    @Column(name = "title")
    private String title;
}
