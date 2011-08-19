begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileContext
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|UnsupportedFileSystemException
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|DefaultContainerExecutor
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|DeletionService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDeletionService
specifier|public
class|class
name|TestDeletionService
block|{
DECL|field|lfs
specifier|private
specifier|static
specifier|final
name|FileContext
name|lfs
init|=
name|getLfs
argument_list|()
decl_stmt|;
DECL|method|getLfs ()
specifier|private
specifier|static
specifier|final
name|FileContext
name|getLfs
parameter_list|()
block|{
try|try
block|{
return|return
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedFileSystemException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|base
specifier|private
specifier|static
specifier|final
name|Path
name|base
init|=
name|lfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestDeletionService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|removeBase ()
specifier|public
specifier|static
name|void
name|removeBase
parameter_list|()
throws|throws
name|IOException
block|{
name|lfs
operator|.
name|delete
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|buildDirs (Random r, Path root, int numpaths)
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|buildDirs
parameter_list|(
name|Random
name|r
parameter_list|,
name|Path
name|root
parameter_list|,
name|int
name|numpaths
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|Path
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numpaths
condition|;
operator|++
name|i
control|)
block|{
name|Path
name|p
init|=
name|root
decl_stmt|;
name|long
name|name
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
do|do
block|{
name|p
operator|=
operator|new
name|Path
argument_list|(
name|p
argument_list|,
literal|""
operator|+
name|name
argument_list|)
expr_stmt|;
name|name
operator|=
name|r
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
literal|0
operator|==
operator|(
name|name
operator|%
literal|2
operator|)
condition|)
do|;
name|ret
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|createDirs (Path base, List<Path> dirs)
specifier|public
name|void
name|createDirs
parameter_list|(
name|Path
name|base
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Path
name|dir
range|:
name|dirs
control|)
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
name|base
argument_list|,
name|dir
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FakeDefaultContainerExecutor
specifier|static
class|class
name|FakeDefaultContainerExecutor
extends|extends
name|DefaultContainerExecutor
block|{
annotation|@
name|Override
DECL|method|deleteAsUser (String user, Path subDir, Path... basedirs)
specifier|public
name|void
name|deleteAsUser
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|Path
modifier|...
name|basedirs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
operator|(
name|Long
operator|.
name|parseLong
argument_list|(
name|subDir
operator|.
name|getName
argument_list|()
argument_list|)
operator|%
literal|2
operator|)
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"dingo"
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|deleteAsUser
argument_list|(
name|user
argument_list|,
name|subDir
argument_list|,
name|basedirs
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|exists
argument_list|(
name|subDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAbsDelete ()
specifier|public
name|void
name|testAbsDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SEED: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|dirs
init|=
name|buildDirs
argument_list|(
name|r
argument_list|,
name|base
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|createDirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"."
argument_list|)
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
name|FakeDefaultContainerExecutor
name|exec
init|=
operator|new
name|FakeDefaultContainerExecutor
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|exec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|DeletionService
name|del
init|=
operator|new
name|DeletionService
argument_list|(
name|exec
argument_list|)
decl_stmt|;
name|del
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|del
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Path
name|p
range|:
name|dirs
control|)
block|{
name|del
operator|.
name|delete
argument_list|(
operator|(
name|Long
operator|.
name|parseLong
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
operator|%
literal|2
operator|)
operator|==
literal|0
condition|?
literal|null
else|:
literal|"dingo"
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|del
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Path
name|p
range|:
name|dirs
control|)
block|{
name|assertFalse
argument_list|(
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRelativeDelete ()
specifier|public
name|void
name|testRelativeDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"SEED: "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|baseDirs
init|=
name|buildDirs
argument_list|(
name|r
argument_list|,
name|base
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|createDirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"."
argument_list|)
argument_list|,
name|baseDirs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|content
init|=
name|buildDirs
argument_list|(
name|r
argument_list|,
operator|new
name|Path
argument_list|(
literal|"."
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|b
range|:
name|baseDirs
control|)
block|{
name|createDirs
argument_list|(
name|b
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
name|DeletionService
name|del
init|=
operator|new
name|DeletionService
argument_list|(
operator|new
name|FakeDefaultContainerExecutor
argument_list|()
argument_list|)
decl_stmt|;
name|del
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|del
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|Path
name|p
range|:
name|content
control|)
block|{
name|assertTrue
argument_list|(
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|baseDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|p
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|del
operator|.
name|delete
argument_list|(
operator|(
name|Long
operator|.
name|parseLong
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
operator|%
literal|2
operator|)
operator|==
literal|0
condition|?
literal|null
else|:
literal|"dingo"
argument_list|,
name|p
argument_list|,
name|baseDirs
operator|.
name|toArray
argument_list|(
operator|new
name|Path
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|del
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Path
name|p
range|:
name|baseDirs
control|)
block|{
for|for
control|(
name|Path
name|q
range|:
name|content
control|)
block|{
name|assertFalse
argument_list|(
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|p
argument_list|,
name|q
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

