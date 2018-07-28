package com.jadepay.cpuminer;


public class LoggerRunnable implements Runnable {

	private CPUMinerApplication application;

	public LoggerRunnable(CPUMinerApplication application) {
		this.application = application;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// Get the hash rate
				int threads = application.getThreads();
				double hashRate = 0;
				for (int i = 0; i < application.getThreads(); i++)
					hashRate += application.getHashRate(i) / 1000;

				// Get the block statistics
				long accepted = application.getAccepted();
				long total = accepted + application.getRejected();

				// Generate the LogEntry
				LogEntry entry = new LogEntry(threads, hashRate, accepted, total);
				application.handleLogEntry(entry);

				// Sleep for a bit
				Thread.sleep(60000);
			} catch (InterruptedException exp) {
				break;
			}
		}
	}
}
