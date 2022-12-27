package lb2.util;

/**
 * Implements a thread with an additional flag indicating cancellation.
 *
 * @author Ruediger Lunde
 * @author Mike Stampone
 */
public class CancellableThread extends Thread {

	private volatile boolean isCancelled;

	public CancellableThread() {
	}

	public CancellableThread(Runnable runnable) {
		super(runnable);
	}

	/**
	 * Returns <code>true</code> if the current thread is cancelled
	 *
	 * @return <code>true</code> if the current thread is cancelled
	 */
	public static boolean currIsCancelled() {
		if(Thread.currentThread() instanceof CancellableThread)
			return ((CancellableThread) Thread.currentThread()).isCancelled;
		return false;
	}

	/**
	 * Returns <code>true</code> if this thread is cancelled
	 *
	 * @return <code>true</code> if this thread is cancelled
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * Cancels this thread
	 */
	public void cancel() {
		isCancelled = true;
	}
}
