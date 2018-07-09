begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.audit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|audit
package|;
end_package

begin_comment
comment|/**  * Enum to define AuditEventStatus values.  */
end_comment

begin_enum
DECL|enum|AuditEventStatus
specifier|public
enum|enum
name|AuditEventStatus
block|{
DECL|enumConstant|SUCCESS
name|SUCCESS
argument_list|(
literal|"SUCCESS"
argument_list|)
block|,
DECL|enumConstant|FAILURE
name|FAILURE
argument_list|(
literal|"FAILURE"
argument_list|)
block|;
DECL|field|status
specifier|private
name|String
name|status
decl_stmt|;
DECL|method|AuditEventStatus (String status)
name|AuditEventStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
DECL|method|getStatus ()
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
block|}
end_enum

end_unit

