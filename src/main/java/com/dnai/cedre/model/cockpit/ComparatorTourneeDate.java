package com.dnai.cedre.model.cockpit;

import java.util.Comparator;

import com.dnai.cedre.domain.Tournee;

public class ComparatorTourneeDate implements Comparator<Tournee>{

	@Override
	public int compare(Tournee arg0, Tournee arg1) {
		return arg0.getDatetournee().compareTo(arg1.getDatetournee());
	}

}
