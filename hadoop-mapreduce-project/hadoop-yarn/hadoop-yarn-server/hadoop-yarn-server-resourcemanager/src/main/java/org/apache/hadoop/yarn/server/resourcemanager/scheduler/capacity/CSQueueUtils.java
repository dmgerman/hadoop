begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
package|;
end_package

begin_class
DECL|class|CSQueueUtils
class|class
name|CSQueueUtils
block|{
DECL|method|checkMaxCapacity (String queueName, float capacity, float maximumCapacity)
specifier|public
specifier|static
name|void
name|checkMaxCapacity
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|maximumCapacity
parameter_list|)
block|{
if|if
condition|(
name|maximumCapacity
operator|!=
name|CapacitySchedulerConfiguration
operator|.
name|UNDEFINED
operator|&&
name|maximumCapacity
operator|<
name|capacity
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal call to setMaxCapacity. "
operator|+
literal|"Queue '"
operator|+
name|queueName
operator|+
literal|"' has "
operator|+
literal|"capacity ("
operator|+
name|capacity
operator|+
literal|") greater than "
operator|+
literal|"maximumCapacity ("
operator|+
name|maximumCapacity
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

