package hydrogenn.firebalance;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSentence implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length<1) {
			return false;
		}
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args[0].equals("new")) {
				if (Firebalance.killList.get(player.getName())==null) {
					player.sendMessage("you haven't even killed anyone recently lol");
				}
				else if (Firebalance.getRemainingTaskTicks("sentenveVoteEnd", null) != null) {
					player.sendMessage("there's already a vote going on!");
				}
				else {
					final String victimName = Firebalance.killList.get(player.getName());
					Firebalance.killList.remove(player.getName());
					BanEntry newBan = Bukkit.getBanList(Type.NAME).getBanEntry(victimName);
					newBan.setReason("You've died recently. §6Your sentence is being set.§r");
					newBan.save();
					Bukkit.broadcastMessage("§6A sentence is being set for "+victimName+". Set your vote with /sentence 'value'");
					Firebalance.addCountedScheduler("sentenceVoteEnd", "server", 4800L, "§6Time until sentence voting ends: ", new Runnable() {
						public void run() {
							Date banDate = new Date();
							long finalTally = getVoteResult();
							banDate.setTime(System.currentTimeMillis()+finalTally-240000L);
				        	Bukkit.getBanList(Type.NAME).addBan(Firebalance.activeSentence, "§6Your sentence has been set. Check the expiration date.§r", banDate, "").save();
							Bukkit.broadcastMessage("§6Sentence finalized. Final result: "+showAsTime(finalTally)+".");
							//5 minutes or less, the default time
							if (finalTally<=300000L) Bukkit.broadcastMessage("§6Come on back, "+victimName+"! You're free!");
							//8 hours or less, or half a waking day; could return later
							else if (finalTally<=28800000L) Bukkit.broadcastMessage("§6We will see you then, "+victimName+".");
							//2 days or less, much can happen but you still hold power
							else if (finalTally<=172800000L) Bukkit.broadcastMessage("§6Hang in there, "+victimName+".");
							//1.5 weeks or less, where you are remembered but your power is removed
							else if (finalTally<=907200000L) Bukkit.broadcastMessage("§6No hard feelings, "+victimName+".");
							//38.25 days or less, slightly over a month; quite a length of time
							else if (finalTally<=3304800000L) Bukkit.broadcastMessage("§6In a while, "+victimName+".");
							//Half a year or less, where most people would forget about the server
							else if (finalTally<=15768000000L) Bukkit.broadcastMessage("§6We hope you return someday, "+victimName+".");
							//For all intents and purposes, this is a permanent ban.
							else Bukkit.broadcastMessage("§6So long, "+victimName+".");
							if (Firebalance.sentenceMaxes.putIfAbsent(victimName, 86400000L+finalTally)!=null)
								Firebalance.sentenceMaxes.put(victimName, Firebalance.sentenceMaxes.get(victimName)+finalTally);
				        	Firebalance.activeSentence = null;
	    					Firebalance.sentenceValues.clear();
			            }
					});
					Firebalance.activeSentence = victimName;
				}
			}
			else if (Firebalance.activeSentence==null) {
				player.sendMessage("nobody is even being sentenced lol");
			}
			else if (args[0].equals("info")) {
				player.sendMessage("§6"+Firebalance.activeSentence+" currently has a sentence of "+showAsTime(getVoteResult()));
				player.sendMessage("§6Vote finalized in "+showAsTime(Firebalance.getRemainingTaskTicks("sentenceVoteEnd", null)*50));
			}
			else {
				long max = Firebalance.sentenceMaxes.getOrDefault(Firebalance.activeSentence, 86400000L);
				List<long[]> output = new ArrayList<long[]>();
				for(String s: args) {
					long[] result = new long[3];
					String[] StringValues = s.split("-");
					List<Long> values = new ArrayList<Long>();
					for (String s2: StringValues) {
						long result2 = 0L;
						if (s2.contains("max")) {
							result2 = max;
						}
						else {
							try {
								if (s2.contains("y")) {
									String s3 = s2.split("y")[0];
									s2 = s2.substring(s3.length()+1);
									result2+=Long.parseLong(s3)*31536000000L;
								}
								if (s2.contains("d")) {
									String s3 = s2.split("d")[0];
									s2 = s2.substring(s3.length()+1);
									result2+=Long.parseLong(s3)*86400000L;
								}
								if (s2.contains("h")) {
									String s3 = s2.split("h")[0];
									s2 = s2.substring(s3.length()+1);
									result2+=Long.parseLong(s3)*3600000L;
								}
								if (s2.contains("m")) {
									String s3 = s2.split("m")[0];
									s2 = s2.substring(s3.length()+1);
									result2+=Long.parseLong(s3)*60000L;
								}
								if (s2.contains("s")) {
									String s3 = s2.split("s")[0];
									s2 = s2.substring(s3.length()+1);
									result2+=Long.parseLong(s3)*1000L;
								}
								if (s2.length()>0) {
									result2+=Long.parseLong(s2)*1000L;
								}
							} catch(NumberFormatException e) {
								if (s2.contains("heyo")) {
									player.sendMessage("heyo, wattup");
									return true;
								}
								if (s2.contains(".")) {
									player.sendMessage("§cThis command does not support decimal values.");
									return true;
								}
								if (s2.contains("/") || s2.contains("\\")) {
									player.sendMessage("§cThis command does not support dates or fractional values.");
									return true;
								}
								else return false;
							}
						}
						values.add(result2);
					}
					result[0]=Collections.min(values);
					result[1]=Collections.max(values);
					result[2]=0L;
					output.add(result);
				}
				if (!isValidVoteEntry(output)) {
					player.sendMessage("Entry contains overlapping lengths.");
					return true;
				}
				Firebalance.sentenceValues.put(player.getName(), output);
				Bukkit.broadcastMessage("§6"+Firebalance.activeSentence+" now has a sentence of "+showAsTime(getVoteResult()));
			}
		}
		return true;
	}
	long getVoteResult() {
		long result[] = {0L,0L};
		int voteCount = 0;
		int playerCount = Bukkit.getOfflinePlayers().length-Bukkit.getBannedPlayers().size();
		long max = Firebalance.sentenceMaxes.getOrDefault(Firebalance.activeSentence, 86400000L);
		long pri = 0L;
		List<long[]> prev = new ArrayList<long[]>();
		for (List<long[]> s: Firebalance.sentenceValues.values()) {
			for (long[] s2: s) {
				long[] sorted = sortItemInList(s2, prev);
				pri = Math.max(pri, sorted[2]);
				prev.add(sorted);
				voteCount++;
			}
		}
		for(long[] s: prev) {
			if (s[2]==pri) {
				long newVote = (s[0]+s[1])/2;
				long maxVote = (voteCount*max*2)/playerCount;
				newVote = Math.min(maxVote, newVote);
				result[0] = (result[0]*result[1] + newVote ) / (result[1]+1);
				result[1]++;
			}
		}
		return result[0];
	}
	long[] sortItemInList(long[] input, List<long[]> list) {
		for (ListIterator<long[]> i = list.listIterator(); i.hasNext();) {
			long[] compare = i.next();
			if (!compare.equals(input)) {
				if ((compare[0]>=input[0]&&compare[0]<=input[1]) || (compare[1]>=input[0]&&compare[1]<=input[1])) {
					long[] greaterVote = new long[3];
					long[] lesserVote = new long[3];
					lesserVote[0] = Math.min(compare[0],input[0]);
					lesserVote[1] = Math.max(compare[1], input[1]);
					lesserVote[2] = compare[2];
					greaterVote[0] = Math.max(compare[0], input[0]);
					greaterVote[1] = Math.min(compare[1], input[1]);
					greaterVote[2] = input[2]+1;
					list.remove(compare);
					list.add(i.previousIndex(),sortItemInList(lesserVote, list));
					input = greaterVote;
				}
				else if (compare[1]>=input[1]&&compare[0]<=input[0]) {
					input[2]++;
				}
			}
		}
		return input;
	}
	boolean isValidVoteEntry(List<long[]> vote) {
		for (long[] input: vote) {
			for (long[] compare: vote) {
				if (!compare.equals(input)) {
					if ((compare[0]>=input[0]&&compare[0]<=compare[1]) || (compare[1]>=input[0]&&compare[1]<=input[1])) {
						return false;
					}
					else if (compare[1]>=input[1]&&compare[0]<=input[0]) {
						return false;
					}
				}
			}
		}
		return true;
	}
	String showAsTime(long banLength) {
		String result = "";
		{
			int gather = 0;
			while(banLength>=31536000000L) {
				gather++;
				banLength-=31536000000L;
			}
			if (gather>0) result+=gather+"y";
		}
		{
			int gather = 0;
			while(banLength>=604800000L) {
				gather++;
				banLength-=604800000L;
			}
			if (gather>0) result+=gather+"w";
		}
		{
			int gather = 0;
			while(banLength>=86400000L) {
				gather++;
				banLength-=86400000L;
			}
			if (gather>0) result+=gather+"d";
		}
		{
			int gather = 0;
			while(banLength>=3600000L) {
				gather++;
				banLength-=3600000L;
			}
			if (gather>0) result+=gather+"h";
		}
		{
			int gather = 0;
			while(banLength>=60000L) {
				gather++;
				banLength-=60000L;
			}
			if (gather>0) result+=gather+"m";
		}
		{
			int gather = 0;
			while(banLength>=1000L) {
				gather++;
				banLength-=1000L;
			}
			if (gather>0) result+=gather+"s";
		}
		if (result.length()==0) result="nothing";
		return result;
	}
}
