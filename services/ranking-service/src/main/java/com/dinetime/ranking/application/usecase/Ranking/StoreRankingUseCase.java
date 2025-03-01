package com.dinetime.ranking.application.usecase.Ranking;

import org.springframework.stereotype.Service;

import com.dinetime.ranking.domain.model.Ranking;
import com.dinetime.ranking.infrastructure.entity.RankingEntity;
import com.dinetime.ranking.infrastructure.repository.redis.RankingRedisRepository;
import com.dinetime.ranking.presentation.request.RankingRequestModel;

@Service
public class StoreRankingUseCase {

    private final RankingRedisRepository rankingRedisRepository;

    public StoreRankingUseCase(RankingRedisRepository rankingRedisRepository) {
        this.rankingRedisRepository = rankingRedisRepository;
    }

    public void execute(RankingRequestModel requestModel) {
        requestModel.getRankings().forEach(entry -> {
            Ranking ranking = new Ranking(entry.getLobbyId(), entry.getMealId(), entry.getScore(), entry.getUserId());

        RankingEntity redisEntity = new RankingEntity(ranking.getLobbyId(),
                                                                ranking.getMealId(), 
                                                                ranking.getScore(),
                                                                ranking.getUserId());

        rankingRedisRepository.storeRanking(redisEntity);
    });
    }
}
