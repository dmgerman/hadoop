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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
import|;
end_import

begin_class
DECL|class|AppAddedSchedulerEvent
specifier|public
class|class
name|AppAddedSchedulerEvent
extends|extends
name|SchedulerEvent
block|{
DECL|field|applicationAttemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|String
name|queue
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|method|AppAddedSchedulerEvent (ApplicationAttemptId applicationAttemptId, String queue, String user)
specifier|public
name|AppAddedSchedulerEvent
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventType
operator|.
name|APP_ADDED
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|applicationAttemptId
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
block|}
end_class

end_unit

