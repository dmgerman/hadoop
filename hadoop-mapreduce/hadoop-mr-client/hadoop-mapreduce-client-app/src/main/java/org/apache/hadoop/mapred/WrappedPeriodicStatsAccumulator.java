begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|//Workaround for PeriodicStateAccumulator being package access
end_comment

begin_class
DECL|class|WrappedPeriodicStatsAccumulator
specifier|public
class|class
name|WrappedPeriodicStatsAccumulator
block|{
DECL|field|real
specifier|private
name|PeriodicStatsAccumulator
name|real
decl_stmt|;
DECL|method|WrappedPeriodicStatsAccumulator (PeriodicStatsAccumulator real)
specifier|public
name|WrappedPeriodicStatsAccumulator
parameter_list|(
name|PeriodicStatsAccumulator
name|real
parameter_list|)
block|{
name|this
operator|.
name|real
operator|=
name|real
expr_stmt|;
block|}
DECL|method|extend (double newProgress, int newValue)
specifier|public
name|void
name|extend
parameter_list|(
name|double
name|newProgress
parameter_list|,
name|int
name|newValue
parameter_list|)
block|{
name|real
operator|.
name|extend
argument_list|(
name|newProgress
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

