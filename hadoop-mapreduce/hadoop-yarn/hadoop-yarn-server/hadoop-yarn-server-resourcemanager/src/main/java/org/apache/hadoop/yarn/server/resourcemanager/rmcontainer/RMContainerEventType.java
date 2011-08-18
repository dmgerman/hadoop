begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmcontainer
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
name|rmcontainer
package|;
end_package

begin_enum
DECL|enum|RMContainerEventType
specifier|public
enum|enum
name|RMContainerEventType
block|{
comment|// Source: scheduler
DECL|enumConstant|START
name|START
block|,
comment|// Source: SchedulerApp
DECL|enumConstant|ACQUIRED
name|ACQUIRED
block|,
DECL|enumConstant|KILL
name|KILL
block|,
comment|// Also from Node on NodeRemoval
DECL|enumConstant|RESERVED
name|RESERVED
block|,
DECL|enumConstant|LAUNCHED
name|LAUNCHED
block|,
DECL|enumConstant|FINISHED
name|FINISHED
block|,
comment|// Source: ApplicationMasterService->Scheduler
DECL|enumConstant|RELEASED
name|RELEASED
block|,
comment|// Source: ContainerAllocationExpirer
DECL|enumConstant|EXPIRE
name|EXPIRE
block|}
end_enum

end_unit

