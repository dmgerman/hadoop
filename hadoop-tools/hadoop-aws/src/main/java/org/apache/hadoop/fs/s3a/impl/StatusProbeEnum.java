begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Enum of probes which can be made of S3.  */
end_comment

begin_enum
DECL|enum|StatusProbeEnum
specifier|public
enum|enum
name|StatusProbeEnum
block|{
comment|/** The actual path. */
DECL|enumConstant|Head
name|Head
block|,
comment|/** HEAD of the path + /. */
DECL|enumConstant|DirMarker
name|DirMarker
block|,
comment|/** LIST under the path. */
DECL|enumConstant|List
name|List
block|;
comment|/** All probes. */
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|StatusProbeEnum
argument_list|>
name|ALL
init|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|StatusProbeEnum
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Skip the HEAD and only look for directories. */
DECL|field|DIRECTORIES
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|StatusProbeEnum
argument_list|>
name|DIRECTORIES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|DirMarker
argument_list|,
name|List
argument_list|)
decl_stmt|;
block|}
end_enum

end_unit

