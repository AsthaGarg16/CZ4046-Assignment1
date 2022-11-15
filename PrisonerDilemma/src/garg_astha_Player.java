class garg_astha_Player extends Player {


    static int[][][] payoff = {
            {{6,3},  //payoffs when first and second players cooperate
                    {3,0}},  //payoffs when first player coops, second defects
            {{8,5},  //payoffs when first player defects, second coops
                    {5,2}}}; //payoffs when first and second players defect

    //variables to store different values
    int score = 0;
    int score1 = 0;
    int score2 = 0;
    int coop1 = 0;
    int coop2 = 0;
    int defect1 = 0;
    int defect2 = 0;
    int punishRound = -1;
    int grudgeRound = 3;

    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
        //Start with cooperation
        if (n==0) return 0;


        // Maintain score for all players
        this.score += payoff[myHistory[n-1]][oppHistory1[n-1]][oppHistory2[n-1]];
        this.score1 += payoff[oppHistory1[n-1]][oppHistory2[n-1]][myHistory[n-1]];
        this.score2 += payoff[oppHistory2[n-1]][oppHistory1[n-1]][myHistory[n-1]];

        // Opponent's cooperation/defection index

        coop1 += oppHistory1[n-1]==1?0:1;
        coop2 += oppHistory2[n-1]==1?0:1;
        defect1 += oppHistory1[n-1];
        defect2 += oppHistory2[n-1];

        //update cooperate probabilities
        double coop_prob1 = (double)coop1/oppHistory1.length;
        double coop_prob2 = (double)coop2/oppHistory2.length;

        //iterate rounds where cooperate to fix punish round
        if (punishRound < -1) {
            punishRound += 1;
            defect1 = 0;
            defect2 = 0;
            return 0;
        }
        if (punishRound > -1 && n == punishRound + grudgeRound + 1) {
            //Count the number of coop during retaliate round to check opp coop level
            int intPlayer1Coop = 0;
            int intPlayer2Coop = 0;

            for (int intCount = 0; intCount < grudgeRound; intCount++) {
                intPlayer1Coop += oppHistory1[n - 1 - intCount] == 0 ? 1 : 0;
                intPlayer2Coop += oppHistory2[n - 1 - intCount] == 0 ? 1 : 0;
            }

            //If both players wish to coop again, start to coop with them
            if (intPlayer1Coop > 1 && intPlayer2Coop > 1 && (oppHistory1[n - 1] + oppHistory2[n - 1]) == 0) {
                //Hold round where agent coop to show intention to coop again
                //Count backwards from -2
                punishRound = -2;
                defect1 = 0;
                defect2 = 0;
                return 0;
            } else {
                punishRound = n;
                return 1;
            }

        }

        //Punish Defection by defecting straight away
        //Stores the round defected
        if (defect1 + defect2 > 0) {
            punishRound = n;
            return 1;
        }

        //towards the end if the opponents are likely to be less cooperative, then defect,
        // or cooperate if our score is higher
        if ((n>=95) && (coop_prob1<0.70 && coop_prob2<0.70)) {
            if(score>score1 && score > score2)
            {
                return 0;
            }
            return 1;
        }
        //if both are inclined to cooperate then cooperate
        else if ((oppHistory1[n-1]+oppHistory2[n-1] == 0)&&(coop_prob1>0.705 && coop_prob2>0.705)) {
            return 0;
        }
        //cooperate by default
        return 0;
    }

}
