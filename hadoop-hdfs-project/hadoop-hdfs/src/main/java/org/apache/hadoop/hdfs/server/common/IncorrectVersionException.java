begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
import|;
end_import

begin_comment
comment|/**  * The exception is thrown when external version does not match   * current version of the application.  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|IncorrectVersionException
specifier|public
class|class
name|IncorrectVersionException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|IncorrectVersionException (String message)
specifier|public
name|IncorrectVersionException
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
DECL|method|IncorrectVersionException (String minimumVersion, String reportedVersion, String remoteDaemon, String thisDaemon)
specifier|public
name|IncorrectVersionException
parameter_list|(
name|String
name|minimumVersion
parameter_list|,
name|String
name|reportedVersion
parameter_list|,
name|String
name|remoteDaemon
parameter_list|,
name|String
name|thisDaemon
parameter_list|)
block|{
name|this
argument_list|(
literal|"The reported "
operator|+
name|remoteDaemon
operator|+
literal|" version is too low to communicate"
operator|+
literal|" with this "
operator|+
name|thisDaemon
operator|+
literal|". "
operator|+
name|remoteDaemon
operator|+
literal|" version: '"
operator|+
name|reportedVersion
operator|+
literal|"' Minimum "
operator|+
name|remoteDaemon
operator|+
literal|" version: '"
operator|+
name|minimumVersion
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
DECL|method|IncorrectVersionException (int currentLayoutVersion, int versionReported, String ofWhat)
specifier|public
name|IncorrectVersionException
parameter_list|(
name|int
name|currentLayoutVersion
parameter_list|,
name|int
name|versionReported
parameter_list|,
name|String
name|ofWhat
parameter_list|)
block|{
name|this
argument_list|(
name|versionReported
argument_list|,
name|ofWhat
argument_list|,
name|currentLayoutVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|IncorrectVersionException (int versionReported, String ofWhat, int versionExpected)
specifier|public
name|IncorrectVersionException
parameter_list|(
name|int
name|versionReported
parameter_list|,
name|String
name|ofWhat
parameter_list|,
name|int
name|versionExpected
parameter_list|)
block|{
name|this
argument_list|(
literal|"Unexpected version "
operator|+
operator|(
name|ofWhat
operator|==
literal|null
condition|?
literal|""
else|:
literal|"of "
operator|+
name|ofWhat
operator|)
operator|+
literal|". Reported: "
operator|+
name|versionReported
operator|+
literal|". Expecting = "
operator|+
name|versionExpected
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

