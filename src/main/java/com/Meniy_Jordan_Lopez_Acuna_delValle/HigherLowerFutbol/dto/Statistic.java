package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.dto;

import lombok.Data;

@Data

public class Statistic {
    private GoalStats goals;
    private CardStats cards;

    public GoalStats getGoals() {
        return goals;
    }

    public void setGoals(GoalStats goals) {
        this.goals = goals;
    }

    public CardStats getCards() {
        return cards;
    }

    public void setCards(CardStats cards) {
        this.cards = cards;
    }
}
