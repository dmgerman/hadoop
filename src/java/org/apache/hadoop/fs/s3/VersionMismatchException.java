begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Thrown when Hadoop cannot read the version of the data stored  * in {@link S3FileSystem}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|VersionMismatchException
specifier|public
class|class
name|VersionMismatchException
extends|extends
name|S3FileSystemException
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
DECL|method|VersionMismatchException (String clientVersion, String dataVersion)
specifier|public
name|VersionMismatchException
parameter_list|(
name|String
name|clientVersion
parameter_list|,
name|String
name|dataVersion
parameter_list|)
block|{
name|super
argument_list|(
literal|"Version mismatch: client expects version "
operator|+
name|clientVersion
operator|+
literal|", but data has version "
operator|+
operator|(
name|dataVersion
operator|==
literal|null
condition|?
literal|"[unversioned]"
else|:
name|dataVersion
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

