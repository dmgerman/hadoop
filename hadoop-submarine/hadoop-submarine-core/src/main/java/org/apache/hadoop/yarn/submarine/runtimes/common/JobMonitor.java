begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|common
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
name|submarine
operator|.
name|common
operator|.
name|ClientContext
import|;
end_import

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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|JobState
import|;
end_import

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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|JobStatus
import|;
end_import

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
name|exceptions
operator|.
name|YarnException
import|;
end_import

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
name|submarine
operator|.
name|common
operator|.
name|exception
operator|.
name|SubmarineException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Monitor status of job(s)  */
end_comment

begin_class
DECL|class|JobMonitor
specifier|public
specifier|abstract
class|class
name|JobMonitor
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JobMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clientContext
specifier|protected
name|ClientContext
name|clientContext
decl_stmt|;
DECL|method|JobMonitor (ClientContext clientContext)
specifier|public
name|JobMonitor
parameter_list|(
name|ClientContext
name|clientContext
parameter_list|)
block|{
name|this
operator|.
name|clientContext
operator|=
name|clientContext
expr_stmt|;
block|}
comment|/**    * Returns status of training job.    *    * @param jobName name of job    * @return job status    * @throws IOException anything else happens    * @throws YarnException anything related to YARN happens    */
DECL|method|getTrainingJobStatus (String jobName)
specifier|public
specifier|abstract
name|JobStatus
name|getTrainingJobStatus
parameter_list|(
name|String
name|jobName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Cleanup AppAdminClient, etc.    */
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/**    * Continue wait and print status if job goes to ready or final state.    * @param jobName    * @throws IOException    * @throws YarnException    * @throws SubmarineException    */
DECL|method|waitTrainingFinal (String jobName)
specifier|public
name|void
name|waitTrainingFinal
parameter_list|(
name|String
name|jobName
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
throws|,
name|SubmarineException
block|{
comment|// Wait 5 sec between each fetch.
name|int
name|waitIntervalSec
init|=
literal|5
decl_stmt|;
name|JobStatus
name|js
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|js
operator|=
name|getTrainingJobStatus
argument_list|(
name|jobName
argument_list|)
expr_stmt|;
name|JobState
name|jobState
init|=
name|js
operator|.
name|getState
argument_list|()
decl_stmt|;
name|js
operator|.
name|nicePrint
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
if|if
condition|(
name|JobState
operator|.
name|isFinal
argument_list|(
name|jobState
argument_list|)
condition|)
block|{
if|if
condition|(
name|jobState
operator|.
name|equals
argument_list|(
name|JobState
operator|.
name|FAILED
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SubmarineException
argument_list|(
literal|"Job failed"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|jobState
operator|.
name|equals
argument_list|(
name|JobState
operator|.
name|KILLED
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SubmarineException
argument_list|(
literal|"Job killed"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Job exited with state="
operator|+
name|jobState
argument_list|)
expr_stmt|;
break|break;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitIntervalSec
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

