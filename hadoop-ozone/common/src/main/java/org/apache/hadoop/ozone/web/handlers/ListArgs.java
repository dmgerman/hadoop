begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|handlers
package|;
end_package

begin_comment
comment|/**  * Supports listing keys with pagination.  */
end_comment

begin_class
DECL|class|ListArgs
specifier|public
class|class
name|ListArgs
parameter_list|<
name|T
extends|extends
name|UserArgs
parameter_list|>
block|{
DECL|field|prevKey
specifier|private
name|String
name|prevKey
decl_stmt|;
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
DECL|field|maxKeys
specifier|private
name|int
name|maxKeys
decl_stmt|;
DECL|field|rootScan
specifier|private
name|boolean
name|rootScan
decl_stmt|;
DECL|field|args
specifier|private
name|T
name|args
decl_stmt|;
comment|/**    * Constructor for ListArgs.    *    * @param args      - BucketArgs    * @param prefix    Prefix to start Query from    * @param maxKeys   Max result set    * @param prevKey - Page token    */
DECL|method|ListArgs (T args, String prefix, int maxKeys, String prevKey)
specifier|public
name|ListArgs
parameter_list|(
name|T
name|args
parameter_list|,
name|String
name|prefix
parameter_list|,
name|int
name|maxKeys
parameter_list|,
name|String
name|prevKey
parameter_list|)
block|{
name|setArgs
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|setMaxKeys
argument_list|(
name|maxKeys
argument_list|)
expr_stmt|;
name|setPrevKey
argument_list|(
name|prevKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copy Constructor for ListArgs.    *    * @param args - List Args    */
DECL|method|ListArgs (T args, ListArgs listArgs)
specifier|public
name|ListArgs
parameter_list|(
name|T
name|args
parameter_list|,
name|ListArgs
name|listArgs
parameter_list|)
block|{
name|this
argument_list|(
name|args
argument_list|,
name|listArgs
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|listArgs
operator|.
name|getMaxKeys
argument_list|()
argument_list|,
name|listArgs
operator|.
name|getPrevKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns page token.    *    * @return String    */
DECL|method|getPrevKey ()
specifier|public
name|String
name|getPrevKey
parameter_list|()
block|{
return|return
name|prevKey
return|;
block|}
comment|/**    * Sets page token.    *    * @param prevKey - Page token    */
DECL|method|setPrevKey (String prevKey)
specifier|public
name|void
name|setPrevKey
parameter_list|(
name|String
name|prevKey
parameter_list|)
block|{
name|this
operator|.
name|prevKey
operator|=
name|prevKey
expr_stmt|;
block|}
comment|/**    * Gets max keys.    *    * @return int    */
DECL|method|getMaxKeys ()
specifier|public
name|int
name|getMaxKeys
parameter_list|()
block|{
return|return
name|maxKeys
return|;
block|}
comment|/**    * Sets max keys.    *    * @param maxKeys - Maximum keys to return    */
DECL|method|setMaxKeys (int maxKeys)
specifier|public
name|void
name|setMaxKeys
parameter_list|(
name|int
name|maxKeys
parameter_list|)
block|{
name|this
operator|.
name|maxKeys
operator|=
name|maxKeys
expr_stmt|;
block|}
comment|/**    * Gets prefix.    *    * @return String    */
DECL|method|getPrefix ()
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**    * Sets prefix.    *    * @param prefix - The prefix that we are looking for    */
DECL|method|setPrefix (String prefix)
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/**    * Gets args.    * @return  T    */
DECL|method|getArgs ()
specifier|public
name|T
name|getArgs
parameter_list|()
block|{
return|return
name|args
return|;
block|}
comment|/**    * Sets  args.    * @param args T    */
DECL|method|setArgs (T args)
specifier|public
name|void
name|setArgs
parameter_list|(
name|T
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
comment|/**    * Checks if we are doing a rootScan.    * @return - RootScan.    */
DECL|method|isRootScan ()
specifier|public
name|boolean
name|isRootScan
parameter_list|()
block|{
return|return
name|rootScan
return|;
block|}
comment|/**    * Sets the RootScan property.    * @param rootScan - Boolean.    */
DECL|method|setRootScan (boolean rootScan)
specifier|public
name|void
name|setRootScan
parameter_list|(
name|boolean
name|rootScan
parameter_list|)
block|{
name|this
operator|.
name|rootScan
operator|=
name|rootScan
expr_stmt|;
block|}
block|}
end_class

end_unit

