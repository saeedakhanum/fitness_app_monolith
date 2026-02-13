package com.project.fitness.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recommendation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type; // type of recommendation

	@Column(length = 2000)
	private String recommendation;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json")
	private List<String> improvements;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json")
	private List<String> suggestions;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json")
	private List<String> safety; // safety instructions

	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	//jab user fetch ho to user immediately fetch na ho and can be fetched explicitly
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name = "user_Id", nullable = false, foreignKey = @ForeignKey(name = "fk_recommendation_user"))
	private User user;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "activity", nullable = false, foreignKey = @ForeignKey(name = "kf_recommendation_activity"))
	private Activity activity;
}
