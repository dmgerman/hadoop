begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.event
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
name|event
package|;
end_package

begin_enum
DECL|enum|SchedulerEventType
specifier|public
enum|enum
name|SchedulerEventType
block|{
comment|// Source: Node
DECL|enumConstant|NODE_ADDED
name|NODE_ADDED
block|,
DECL|enumConstant|NODE_REMOVED
name|NODE_REMOVED
block|,
DECL|enumConstant|NODE_UPDATE
name|NODE_UPDATE
block|,
comment|// Source: App
DECL|enumConstant|APP_ADDED
name|APP_ADDED
block|,
DECL|enumConstant|APP_REMOVED
name|APP_REMOVED
block|,
comment|// Source: ContainerAllocationExpirer
DECL|enumConstant|CONTAINER_EXPIRED
name|CONTAINER_EXPIRED
block|}
end_enum

end_unit

