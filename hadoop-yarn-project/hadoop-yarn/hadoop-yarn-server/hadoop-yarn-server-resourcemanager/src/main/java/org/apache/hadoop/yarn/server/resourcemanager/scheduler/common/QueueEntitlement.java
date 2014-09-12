begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common
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
name|common
package|;
end_package

begin_class
DECL|class|QueueEntitlement
specifier|public
class|class
name|QueueEntitlement
block|{
DECL|field|capacity
specifier|private
name|float
name|capacity
decl_stmt|;
DECL|field|maxCapacity
specifier|private
name|float
name|maxCapacity
decl_stmt|;
DECL|method|QueueEntitlement (float capacity, float maxCapacity)
specifier|public
name|QueueEntitlement
parameter_list|(
name|float
name|capacity
parameter_list|,
name|float
name|maxCapacity
parameter_list|)
block|{
name|this
operator|.
name|setCapacity
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxCapacity
operator|=
name|maxCapacity
expr_stmt|;
block|}
DECL|method|getMaxCapacity ()
specifier|public
name|float
name|getMaxCapacity
parameter_list|()
block|{
return|return
name|maxCapacity
return|;
block|}
DECL|method|setMaxCapacity (float maxCapacity)
specifier|public
name|void
name|setMaxCapacity
parameter_list|(
name|float
name|maxCapacity
parameter_list|)
block|{
name|this
operator|.
name|maxCapacity
operator|=
name|maxCapacity
expr_stmt|;
block|}
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
DECL|method|setCapacity (float capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
block|}
block|}
end_class

end_unit

