begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.server.dns
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|server
operator|.
name|dns
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xbill
operator|.
name|DNS
operator|.
name|Zone
import|;
end_import

begin_comment
comment|/**  * A selector that returns the zone associated with a provided name.  */
end_comment

begin_interface
DECL|interface|ZoneSelector
specifier|public
interface|interface
name|ZoneSelector
block|{
comment|/**    * Finds the best matching zone given the provided name.    * @param name the record name for which a zone is requested.    * @return the matching zone.    */
DECL|method|findBestZone (Name name)
name|Zone
name|findBestZone
parameter_list|(
name|Name
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

