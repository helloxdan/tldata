/**
 * This file is part of Support Bot.
 *
 *     Foobar is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.telegram.plugins.xuser.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.bot.kernel.database.DatabaseManager;
import org.telegram.bot.kernel.differenceparameters.IDifferenceParametersService;

/**
 * 重写默认的DifferenceParametersService.java，支持多个账号同时登录。<br>
 * 原来的方法，使用了static变量，导致冲突。
 * 
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Handler for difference information
 * @date 14 of February of 2016
 */
public class DifferenceParametersService implements
		IDifferenceParametersService {

	// private static final ConcurrentHashMap<Integer,
	// DifferenceParametersService.DifferenceData> differenceDatas = new
	// ConcurrentHashMap<>();
	// private static final AtomicBoolean loaded = new AtomicBoolean(false);
	// 每个实例单独使用
	private final ConcurrentHashMap<Integer, DifferenceParametersService.DifferenceData> differenceDatas = new ConcurrentHashMap<>();
	private final AtomicBoolean loaded = new AtomicBoolean(false);

	private static final Object lock = new Object();
	private final DatabaseManager databaseManager;

	private String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public DifferenceParametersService(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
		if (!loaded.get()) {
			loadParamsFromDatabase();
		}
	}

	private ConcurrentHashMap<Integer, DifferenceData> getDifferenceDatas() {
		return differenceDatas;
	}

	@Override
	public void setNewUpdateParams(int chatId, @Nullable Integer newPts,
			@Nullable Integer newSeq, @NotNull Integer newDate) {
		if (!getDifferenceDatas().containsKey(chatId)) {
			create(chatId);
		}
		synchronized (lock) {
			getDifferenceDatas().get(chatId).pts = ((newPts == null) || (newPts == 0)) ? getDifferenceDatas()
					.get(chatId).pts : newPts;
			getDifferenceDatas().get(chatId).seq = ((newSeq == null) || (newSeq == 0)) ? getDifferenceDatas()
					.get(chatId).seq : newSeq;
			getDifferenceDatas().get(chatId).date = (newDate < getDifferenceDatas()
					.get(chatId).date) ? getDifferenceDatas().get(chatId).date
					: newDate;
			databaseManager.updateDifferencesData(chatId, getDifferenceDatas()
					.get(chatId).pts, getDifferenceDatas().get(chatId).date,
					getDifferenceDatas().get(chatId).seq);
		}
	}

	@Override
	public int getDate(int chatId) {
		if (!getDifferenceDatas().containsKey(chatId)) {
			create(chatId);
		}
		return getDifferenceDatas().get(chatId).date;
	}

	@Override
	public int getPts(int chatId) {
		if (!getDifferenceDatas().containsKey(chatId)) {
			create(chatId);
		}
		return getDifferenceDatas().get(chatId).pts;
	}

	@Override
	public int getSeq(int chatId) {
		if (!getDifferenceDatas().containsKey(chatId)) {
			create(chatId);
		}
		return getDifferenceDatas().get(chatId).seq;
	}

	@Override
	public boolean mustGetDifferences(int chatId, int pts,
			@Nullable Integer ptsCount, int seq, @Nullable Integer seqStart) {
		synchronized (lock) {
			boolean mustGetDifferences = false;
			if (pts > 0) {
				final int newPts = getPts(chatId)
						+ ((ptsCount == null) ? 0 : ptsCount);
				if (newPts != pts) {
					mustGetDifferences = true;
				}
			} else if (seq > 0) {
				final int newSeqStart = (seqStart == null) ? seq : seqStart;
				if ((newSeqStart != (getSeq(chatId) + 1))
						&& (newSeqStart > getSeq(chatId))) {
					mustGetDifferences = true;
				}
			}
			/*
			 * if ((seq != -1) && (seq != 0) && ((this.seq + 1) < seq)) {
			 * mustGetDifferences = true; } if ((this.pts != -1) && ((this.pts +
			 * ptsCount) < pts)) { mustGetDifferences = true; }
			 */

			return mustGetDifferences;
		}
	}

	private void loadParamsFromDatabase() {
		synchronized (lock) {
			final Map<Integer, int[]> differencesDatas = databaseManager
					.getDifferencesData();
			for (Map.Entry<Integer, int[]> entry : differencesDatas.entrySet()) {
				final DifferenceParametersService.DifferenceData data = new DifferenceParametersService.DifferenceData();
				data.pts = entry.getValue()[0];
				data.date = entry.getValue()[1];
				data.seq = entry.getValue()[2];
				getDifferenceDatas().put(entry.getKey(), data);
			}
			loaded.set(true);
		}
	}

	private void create(int chatId) {
		synchronized (lock) {
			if (!getDifferenceDatas().containsKey(chatId)) {
				getDifferenceDatas().put(chatId,
						new DifferenceParametersService.DifferenceData());
				databaseManager.updateDifferencesData(chatId, 0, 0, 0);
			}
		}
	}

	private class DifferenceData {
		int pts;
		int date;
		int seq;

		DifferenceData() {
			pts = 0;
			date = 0;
			seq = 0;
		}
	}
}
