package com.debaets.crud.core.service.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long start;
    private Long end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
