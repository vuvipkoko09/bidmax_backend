package com.example.daugiaonline.entity;

import com.example.daugiaonline.enums.AuctionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Auction extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "start_price", nullable = false)
    private Double startPrice;

    @Column(name = "current_price", nullable = false)
    private Double currentPrice;

    @Column(name = "step_price", nullable = false)
    private Double stepPrice;

    @Column(name = "deposit_amount", nullable = false)
    private Double depositAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AuctionStatus status;

    @Column(name = "reg_start_time", nullable = false)
    private LocalDateTime regStartTime;

    @Column(name = "reg_end_time", nullable = false)
    private LocalDateTime regEndTime;

    @Column(name = "bid_start_time", nullable = false)
    private LocalDateTime bidStartTime;

    @Column(name = "bid_end_time", nullable = false)
    private LocalDateTime bidEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(name = "winning_price")
    private Double winningPrice;

    @Version
    private Long version;
}
