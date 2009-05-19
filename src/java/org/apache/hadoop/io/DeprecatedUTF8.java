begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Wrapper for {@link UTF8}.  * This class should be used only when it is absolutely necessary  * to use {@link UTF8}. The only difference is that using this class  * does not require "@SuppressWarning" annotation to avoid javac warning.   * Instead the deprecation is implied in the class name.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|DeprecatedUTF8
specifier|public
class|class
name|DeprecatedUTF8
extends|extends
name|UTF8
block|{
DECL|method|DeprecatedUTF8 ()
specifier|public
name|DeprecatedUTF8
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Construct from a given string. */
DECL|method|DeprecatedUTF8 (String string)
specifier|public
name|DeprecatedUTF8
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|super
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
comment|/** Construct from a given string. */
DECL|method|DeprecatedUTF8 (DeprecatedUTF8 utf8)
specifier|public
name|DeprecatedUTF8
parameter_list|(
name|DeprecatedUTF8
name|utf8
parameter_list|)
block|{
name|super
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
block|}
comment|/* The following two are the mostly commonly used methods.    * wrapping them so that editors do not complain about the deprecation.    */
DECL|method|readString (DataInput in)
specifier|public
specifier|static
name|String
name|readString
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|UTF8
operator|.
name|readString
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|writeString (DataOutput out, String s)
specifier|public
specifier|static
name|int
name|writeString
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|UTF8
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit

