begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
name|rmapp
operator|.
name|attempt
package|;
end_package

begin_enum
DECL|enum|RMAppAttemptEventType
specifier|public
enum|enum
name|RMAppAttemptEventType
block|{
comment|// Source: RMApp
DECL|enumConstant|START
name|START
block|,
DECL|enumConstant|KILL
name|KILL
block|,
comment|// Source: AMLauncher
DECL|enumConstant|LAUNCHED
name|LAUNCHED
block|,
DECL|enumConstant|LAUNCH_FAILED
name|LAUNCH_FAILED
block|,
comment|// Source: AMLivelinessMonitor
DECL|enumConstant|EXPIRE
name|EXPIRE
block|,
comment|// Source: ApplicationMasterService
DECL|enumConstant|REGISTERED
name|REGISTERED
block|,
DECL|enumConstant|STATUS_UPDATE
name|STATUS_UPDATE
block|,
DECL|enumConstant|UNREGISTERED
name|UNREGISTERED
block|,
comment|// Source: Containers
DECL|enumConstant|CONTAINER_ACQUIRED
name|CONTAINER_ACQUIRED
block|,
DECL|enumConstant|CONTAINER_ALLOCATED
name|CONTAINER_ALLOCATED
block|,
DECL|enumConstant|CONTAINER_FINISHED
name|CONTAINER_FINISHED
block|,
comment|// Source: Scheduler
DECL|enumConstant|APP_REJECTED
name|APP_REJECTED
block|,
DECL|enumConstant|APP_ACCEPTED
name|APP_ACCEPTED
block|,  }
end_enum

end_unit

