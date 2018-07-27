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
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|ApplicationMasterProtocol
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|ResourceRequest
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when a resource requested via  * {@link ResourceRequest} in the  * {@link ApplicationMasterProtocol#allocate(AllocateRequest)} API is out of the  * range of the configured lower and upper limits on resources.  *   */
end_comment

begin_class
DECL|class|InvalidResourceRequestException
specifier|public
class|class
name|InvalidResourceRequestException
extends|extends
name|YarnException
block|{
DECL|field|LESS_THAN_ZERO_RESOURCE_MESSAGE_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|LESS_THAN_ZERO_RESOURCE_MESSAGE_TEMPLATE
init|=
literal|"Invalid resource request! Cannot allocate containers as "
operator|+
literal|"requested resource is less than 0! "
operator|+
literal|"Requested resource type=[%s], "
operator|+
literal|"Requested resource=%s"
decl_stmt|;
DECL|field|GREATER_THAN_MAX_RESOURCE_MESSAGE_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|GREATER_THAN_MAX_RESOURCE_MESSAGE_TEMPLATE
init|=
literal|"Invalid resource request! Cannot allocate containers as "
operator|+
literal|"requested resource is greater than "
operator|+
literal|"maximum allowed allocation. "
operator|+
literal|"Requested resource type=[%s], "
operator|+
literal|"Requested resource=%s, maximum allowed allocation=%s, "
operator|+
literal|"please note that maximum allowed allocation is calculated "
operator|+
literal|"by scheduler based on maximum resource of registered "
operator|+
literal|"NodeManagers, which might be less than configured "
operator|+
literal|"maximum allocation=%s"
decl_stmt|;
DECL|field|UNKNOWN_REASON_MESSAGE_TEMPLATE
specifier|public
specifier|static
specifier|final
name|String
name|UNKNOWN_REASON_MESSAGE_TEMPLATE
init|=
literal|"Invalid resource request! "
operator|+
literal|"Cannot allocate containers for an unknown reason! "
operator|+
literal|"Requested resource type=[%s], Requested resource=%s"
decl_stmt|;
DECL|enum|InvalidResourceType
specifier|public
enum|enum
name|InvalidResourceType
block|{
DECL|enumConstant|LESS_THAN_ZERO
DECL|enumConstant|GREATER_THEN_MAX_ALLOCATION
DECL|enumConstant|UNKNOWN
name|LESS_THAN_ZERO
block|,
name|GREATER_THEN_MAX_ALLOCATION
block|,
name|UNKNOWN
block|;   }
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|13498237L
decl_stmt|;
DECL|field|invalidResourceType
specifier|private
specifier|final
name|InvalidResourceType
name|invalidResourceType
decl_stmt|;
DECL|method|InvalidResourceRequestException (Throwable cause)
specifier|public
name|InvalidResourceRequestException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|invalidResourceType
operator|=
name|InvalidResourceType
operator|.
name|UNKNOWN
expr_stmt|;
block|}
DECL|method|InvalidResourceRequestException (String message)
specifier|public
name|InvalidResourceRequestException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|InvalidResourceType
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
DECL|method|InvalidResourceRequestException (String message, InvalidResourceType invalidResourceType)
specifier|public
name|InvalidResourceRequestException
parameter_list|(
name|String
name|message
parameter_list|,
name|InvalidResourceType
name|invalidResourceType
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|invalidResourceType
operator|=
name|invalidResourceType
expr_stmt|;
block|}
DECL|method|InvalidResourceRequestException (String message, Throwable cause)
specifier|public
name|InvalidResourceRequestException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|invalidResourceType
operator|=
name|InvalidResourceType
operator|.
name|UNKNOWN
expr_stmt|;
block|}
DECL|method|getInvalidResourceType ()
specifier|public
name|InvalidResourceType
name|getInvalidResourceType
parameter_list|()
block|{
return|return
name|invalidResourceType
return|;
block|}
block|}
end_class

end_unit

