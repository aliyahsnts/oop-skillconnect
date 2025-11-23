## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

---

# Setting Up Java Development in Visual Studio Code

This guide walks you through installing Java, configuring your environment, pulling your project, and running it inside Visual Studio Code (VS Code).

### Prerequisites

- Windows (64-bit)
- VS Code installed
- Internet connection

## 1. Install Java (JDK)

1. Download the Oracle JDK (x64 Installer) from the official Oracle website.
   https://www.oracle.com/java/technologies/downloads/#jdk25-windows

2. Run the installer and follow the prompts.

3. Wait for the installation to fully complete.
   Note: VS Code requires a JDK (not just a JRE).

## 2. Verify Java Installation

Open Command Prompt or PowerShell and run:

<pre> 
java -version
javac -version 
</pre>

If both commands show version numbers, Java is installed correctly.

## 3. Install Java Extensions in VS Code (not necessary)

Open VS Code and install:

- Extension Pack for Java (recommended)

It includes:

- Language Support for Java™ by Red Hat
- Debugger for Java
- Test Runner for Java
- Maven / Gradle support

Search “Java” in the Extensions Marketplace and install the pack.

_(The above extension is not neccesary. I recommend that you have Code Runner installed from our C++ project.)_

## 4. Pull our Java project

Clone our repository:

<pre> git clone https://github.com/aliyahsnts/oop-skillconnect </pre>

Then open the project folder in VS Code:
`File → Open Folder → select your project (create a folder if you haven't already)`

## 5. Run the Java Project

Option A — Using the VS Code Run Button (usual method)

- Open your main Java file (Main.java or similar)
- Click Run or the play ▶️ button that appears above the main method

Option B — Using the VS Code Debug Panel

- Open the Run and Debug panel
- Select Java configuration
- Press Start Debugging

## 6. If Your Project Doesn't Run: Fix PATH Configuration

_(you can ask for my help for this step)_

Sometimes VS Code or the terminal cannot find java or javac.

### Find where Java is installed

Common paths for Oracle JDK:
`C:\Program Files\Java\jdk-<version>\`

Make note of:

- bin folder, e.g.
  C:\Program Files\Java\jdk-<version>\bin

### Add Java to your PATH

1. Open Start Menu → “Edit the system environment variables”
2. Click Environment Variables
3. Under System variables, edit Path
4. Add:
   `C:\Program Files\Java\jdk-<version>\bin`

Restart VS Code after applying changes.

## 7. Done!

Your Java environment should now be fully configured.
You can build, run, and debug Java projects in Visual Studio Code.

If there are any issues, contact me for help.

---

# How to Push Your Code to Our GitHub Repo

1. Make sure you have **saved all your changes**.
2. Check if the repo is right (output should be our repo link)
   <pre> git remote -v </pre>

   You only need to do this **once**. If you are sure you are on the right repository, you don't have to do this step again.

3. **Add** all the files / Stage your changes
<pre> git add . </pre>

4. Write your **commit message**
<pre> git commit -m "[your message here (summarize the changes you made)]"  </pre>

5. **Push** your changes to main branch
<pre> git push -u origin main </pre>
