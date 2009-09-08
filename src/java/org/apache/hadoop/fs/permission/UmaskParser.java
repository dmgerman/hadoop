begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Parse umask value provided as a string, either in octal or symbolic  * format and return it as a short value. Umask values are slightly  * different from standard modes as they cannot specify sticky bit  * or X.  *  */
end_comment

begin_class
DECL|class|UmaskParser
class|class
name|UmaskParser
extends|extends
name|PermissionParser
block|{
DECL|field|chmodOctalPattern
specifier|private
specifier|static
name|Pattern
name|chmodOctalPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*[+]?()([0-7]{3})\\s*$"
argument_list|)
decl_stmt|;
comment|// no leading 1 for sticky bit
DECL|field|umaskSymbolicPattern
specifier|private
specifier|static
name|Pattern
name|umaskSymbolicPattern
init|=
comment|/* not allow X or t */
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\G\\s*([ugoa]*)([+=-]+)([rwx]+)([,\\s]*)\\s*"
argument_list|)
decl_stmt|;
DECL|field|umaskMode
specifier|final
name|short
name|umaskMode
decl_stmt|;
DECL|method|UmaskParser (String modeStr)
specifier|public
name|UmaskParser
parameter_list|(
name|String
name|modeStr
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|super
argument_list|(
name|modeStr
argument_list|,
name|umaskSymbolicPattern
argument_list|,
name|chmodOctalPattern
argument_list|)
expr_stmt|;
name|umaskMode
operator|=
operator|(
name|short
operator|)
name|combineModes
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getUMask ()
specifier|public
name|short
name|getUMask
parameter_list|()
block|{
return|return
name|umaskMode
return|;
block|}
block|}
end_class

end_unit

