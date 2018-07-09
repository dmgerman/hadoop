begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
comment|/**  * Enumeration for defining types of Audit Loggers in Ozone.  */
end_comment

begin_enum
DECL|enum|AuditLoggerType
specifier|public
enum|enum
name|AuditLoggerType
block|{
DECL|enumConstant|DNLOGGER
name|DNLOGGER
argument_list|(
literal|"DNAudit"
argument_list|)
block|,
DECL|enumConstant|OMLOGGER
name|OMLOGGER
argument_list|(
literal|"OMAudit"
argument_list|)
block|,
DECL|enumConstant|SCMLOGGER
name|SCMLOGGER
argument_list|(
literal|"SCMAudit"
argument_list|)
block|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|AuditLoggerType (String type)
name|AuditLoggerType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
block|}
end_enum

end_unit

