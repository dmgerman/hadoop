begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|hdfs
operator|.
name|DFSConfigKeys
import|;
end_import

begin_comment
comment|/**  * This class contains constants for configuration keys used  * in WebHdfs.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|WebHdfsConfigKeys
specifier|public
class|class
name|WebHdfsConfigKeys
extends|extends
name|DFSConfigKeys
block|{
comment|/** User name pattern key */
DECL|field|USER_PATTERN_KEY
specifier|public
specifier|static
specifier|final
name|String
name|USER_PATTERN_KEY
init|=
literal|"webhdfs.user.provider.user.pattern"
decl_stmt|;
comment|/** Default user name pattern value */
DECL|field|USER_PATTERN_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|USER_PATTERN_DEFAULT
init|=
literal|"^[A-Za-z_][A-Za-z0-9._-]*[$]?$"
decl_stmt|;
block|}
end_class

end_unit

