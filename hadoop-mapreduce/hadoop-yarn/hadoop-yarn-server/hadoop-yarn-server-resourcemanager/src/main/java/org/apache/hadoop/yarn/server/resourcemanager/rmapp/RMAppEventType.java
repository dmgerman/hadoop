begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
package|;
end_package

begin_enum
DECL|enum|RMAppEventType
specifier|public
enum|enum
name|RMAppEventType
block|{
comment|// Source: ClientRMService
DECL|enumConstant|START
name|START
block|,
DECL|enumConstant|KILL
name|KILL
block|,
comment|// Source: RMAppAttempt
DECL|enumConstant|APP_REJECTED
name|APP_REJECTED
block|,
DECL|enumConstant|APP_ACCEPTED
name|APP_ACCEPTED
block|,
DECL|enumConstant|ATTEMPT_REGISTERED
name|ATTEMPT_REGISTERED
block|,
DECL|enumConstant|ATTEMPT_FINISHED
name|ATTEMPT_FINISHED
block|,
comment|// Will send the final state
DECL|enumConstant|ATTEMPT_FAILED
name|ATTEMPT_FAILED
block|,
DECL|enumConstant|ATTEMPT_KILLED
name|ATTEMPT_KILLED
block|}
end_enum

end_unit

