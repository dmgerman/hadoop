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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Marker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|MarkerManager
import|;
end_import

begin_comment
comment|/**  * Defines audit marker types.  */
end_comment

begin_enum
DECL|enum|AuditMarker
specifier|public
enum|enum
name|AuditMarker
block|{
DECL|enumConstant|WRITE
DECL|enumConstant|MarkerManager.getMarker
name|WRITE
argument_list|(
name|MarkerManager
operator|.
name|getMarker
argument_list|(
literal|"WRITE"
argument_list|)
argument_list|)
block|,
DECL|enumConstant|READ
DECL|enumConstant|MarkerManager.getMarker
name|READ
argument_list|(
name|MarkerManager
operator|.
name|getMarker
argument_list|(
literal|"READ"
argument_list|)
argument_list|)
block|;
DECL|field|marker
specifier|private
name|Marker
name|marker
decl_stmt|;
DECL|method|AuditMarker (Marker marker)
name|AuditMarker
parameter_list|(
name|Marker
name|marker
parameter_list|)
block|{
name|this
operator|.
name|marker
operator|=
name|marker
expr_stmt|;
block|}
DECL|method|getMarker ()
specifier|public
name|Marker
name|getMarker
parameter_list|()
block|{
return|return
name|marker
return|;
block|}
block|}
end_enum

end_unit

