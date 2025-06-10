package com.DreamOfDuck.pang.entity;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Item")
public class Item extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="itemId")
    private Long id;

    private Integer dia;
    private Integer feather;

    @OneToOne
    @JoinColumn(name="memberId")
    private Member host;

}
