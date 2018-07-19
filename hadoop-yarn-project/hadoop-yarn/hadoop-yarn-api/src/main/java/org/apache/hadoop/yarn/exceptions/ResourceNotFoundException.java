begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|exception
operator|.
name|ExceptionUtils
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|api
operator|.
name|records
operator|.
name|Resource
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when details of an unknown resource type  * are requested.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ResourceNotFoundException
specifier|public
class|class
name|ResourceNotFoundException
extends|extends
name|YarnRuntimeException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|10081982L
decl_stmt|;
DECL|field|MESSAGE
specifier|private
specifier|static
specifier|final
name|String
name|MESSAGE
init|=
literal|"The resource manager encountered a "
operator|+
literal|"problem that should not occur under normal circumstances. "
operator|+
literal|"Please report this error to the Hadoop community by opening a "
operator|+
literal|"JIRA ticket at http://issues.apache.org/jira and including the "
operator|+
literal|"following information:%n* Resource type requested: %s%n* Resource "
operator|+
literal|"object: %s%n* The stack trace for this exception: %s%n"
operator|+
literal|"After encountering this error, the resource manager is "
operator|+
literal|"in an inconsistent state. It is safe for the resource manager "
operator|+
literal|"to be restarted as the error encountered should be transitive. "
operator|+
literal|"If high availability is enabled, failing over to "
operator|+
literal|"a standby resource manager is also safe."
decl_stmt|;
DECL|method|ResourceNotFoundException (Resource resource, String type)
specifier|public
name|ResourceNotFoundException
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MESSAGE
argument_list|,
name|type
argument_list|,
name|resource
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
operator|new
name|Exception
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceNotFoundException (Resource resource, String type, Throwable cause)
specifier|public
name|ResourceNotFoundException
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|String
name|type
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|MESSAGE
argument_list|,
name|type
argument_list|,
name|resource
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|cause
argument_list|)
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceNotFoundException (String message)
specifier|public
name|ResourceNotFoundException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

