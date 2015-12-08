package util;

public interface IOCopyCondition
{
	public static final NOOPCondition NOOPCONDITION = new NOOPCondition();

	/**
	 * Sets the start time of the copy operation
	 * @param startTime
	 */
	public void setStartTime(long startTime);

	/**
	 * apply the condition check while the copy operation. pass the copied length to check condition.
	 * return true if pass the condition
	 * false fail to pass the condition. copy operation will stopped.
	 * @param copiedLength
	 */
	public boolean applyCondition(long copiedLength);

	public int getBufferSize();

	public static class NOOPCondition implements IOCopyCondition
	{
		public boolean applyCondition(long copiedLength)
		{
			return true;
		}

		public int getBufferSize()
		{
			return IOUtils.DEFULT_BUFFER_LENGHT;
		}

		public void setStartTime(long starttime)
		{}
	}

	public static class SpeedLimitCondition extends NOOPCondition
	{
		protected int bufferSize = IOUtils.DEFULT_BUFFER_LENGHT;
		protected long limitBandwidth;
		protected long sleepoffset;
		protected long tick;

		public SpeedLimitCondition(long limitBandwidth)
		{
			this.limitBandwidth = this.sleepoffset = limitBandwidth;
		}

		public SpeedLimitCondition(long limitBandwidth, int bufferSize)
		{
			this.limitBandwidth = this.sleepoffset = limitBandwidth;
			this.bufferSize = bufferSize;
		}

		public boolean applyCondition(long copiedLength)
		{
			if ( copiedLength > sleepoffset )
			{
				sleepoffset+= limitBandwidth;
				long sleepMillis = 1000 - Math.min(System.currentTimeMillis() - tick, 1000);
				if ( sleepMillis > 0 )
				{
					ThreadUtils.sleepQuietly(sleepMillis);
				}
				tick = System.currentTimeMillis();
			}
			return true;
		}

		public int getBufferSize()
		{
			return bufferSize;
		}

		public void setStartTime(long startTime)
		{
			tick = startTime;
		}
	}

	public static class SpeedLimitByLengthCondition extends SpeedLimitCondition
	{
		protected long tiggerLength;

		public SpeedLimitByLengthCondition(long limitBandwidth, long tiggerLength)
		{
			super(limitBandwidth);
			super.sleepoffset = this.tiggerLength = tiggerLength;
		}

		public SpeedLimitByLengthCondition(long limitBandwidth, long tiggerLength, int bufferSize)
		{
			super(limitBandwidth, bufferSize);
			super.sleepoffset = this.tiggerLength = tiggerLength;
		}

		public boolean applyCondition(long copiedLength)
		{
			if ( copiedLength > tiggerLength )
			{
				return super.applyCondition(copiedLength);
			}
			else
			{
				return true;
			}
		}
	}
}
;