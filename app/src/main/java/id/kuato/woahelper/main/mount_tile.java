package id.kuato.woahelper.main;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.topjohnwu.superuser.ShellUtils;

import id.kuato.woahelper.R;
import id.kuato.woahelper.preference.pref;

public class mount_tile extends TileService {

	String win;
	String findwin;
	String mnt_stat;
	String winpath;

	@Override
	public void onStartListening() {
		super.onStartListening();
		update();
	}

	// Called when the user taps on your tile in an active or inactive state.
	@Override
	public void onClick() {
		super.onClick();
		if (mnt_stat.isEmpty()) mount();
		else unmount();
		update();
	}

	private void update() {
		final Tile tile = getQsTile();
		if (isSecure() && !pref.getSecure(this)){ tile.setState(0);return;}
		findwin = ShellUtils.fastCmd("su -mm -c find /dev/block | grep win");
		if (findwin.isEmpty()) findwin = ShellUtils.fastCmd("su -mm -c find /dev/block | grep mindows");
		if (findwin.isEmpty()) findwin = ShellUtils.fastCmd("su -mm -c find /dev/block | grep windows");
		if (findwin.isEmpty()) findwin = ShellUtils.fastCmd("su -mm -c find /dev/block | grep Win");
		if (findwin.isEmpty()) findwin = ShellUtils.fastCmd("su -mm -c find /dev/block | grep Mindows");
		if (findwin.isEmpty()) findwin = ShellUtils.fastCmd("su -mm -c find /dev/block | grep Windows");
		win = ShellUtils.fastCmd("su -mm -c realpath " + findwin);
		winpath = (pref.getMountLocation(this) ? "/mnt/Windows" : "/mnt/sdcard/Windows");
		mnt_stat = ShellUtils.fastCmd("su -mm -c mount | grep " + win);
		if (mnt_stat.isEmpty()) tile.setState(1);
		else tile.setState(2);
		tile.updateTile();
	}

	private void mount() {
		ShellUtils.fastCmd("su -mm -c mkdir " + winpath + " || true");
		ShellUtils.fastCmd("cd /data/data/id.kuato.woahelper/files");
		ShellUtils.fastCmd("su -mm -c ./mount.ntfs " + win + " " + winpath);
	}
	
	public void unmount() {
		ShellUtils.fastCmd("su -mm -c umount " + winpath);
		ShellUtils.fastCmd("rmdir " + winpath);
	}


}
